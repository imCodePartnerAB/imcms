package com.imcode.imcms.servlet;

import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.servlet.ImageHandling.SourceFile;
import imcode.server.document.FileDocumentDomainObject;

public class RebuildImageCache extends HttpServlet {
    private static final long LOG_PROGRESS_INTERVAL_MILLISECONDS = 1 * 60 * 1000;
    
    private static RebuildImageCacheThread rebuildThread;
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        
        if (!cms.getCurrentUser().isSuperAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            
            return;
        }
        
        boolean stop = request.getParameter("stop") != null;
        String message = null;
        
        synchronized (RebuildImageCache.class) {
            if (stop) {
                if (rebuildThread != null && rebuildThread.isAlive()) {
                    rebuildThread.setCancelled(true);
                    rebuildThread.interrupt();
                    rebuildThread = null;
                    
                    message = "Image cache rebuild has been stopped";
                    
                } else {
                    message = "Image cache rebuild wasn't running";
                    
                }
            } else {
                if (rebuildThread == null || !rebuildThread.isAlive()) {
                    rebuildThread = new RebuildImageCacheThread(LOG_PROGRESS_INTERVAL_MILLISECONDS);
                    rebuildThread.start();
                    
                    message = "Image cache rebuild has been started";
                    
                } else {
                    message = "Image cache rebuild is already running";
                    
                }
            }
        }
        
        byte[] data = message.getBytes("UTF-8");
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(data.length);
        
        OutputStream output = null;
        try {
            output = response.getOutputStream();
            
            output.write(data);
            output.flush();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}

class RebuildImageCacheThread extends Thread {
    private static final Logger log = Logger.getLogger(RebuildImageCacheThread.class);
    
    private volatile boolean cancelled;
    private long logInterval;
    
    
    public RebuildImageCacheThread(long logInterval) {
        super("RebuildImageCache");
        
        this.logInterval = logInterval;
    }
    
    @Override
    public void run() {
        Config config = Imcms.getServices().getConfig();
        
        Map<Integer, Map<Integer, ImageDomainObject>> documentImages = Imcms.getServices().getImageCacheMapper().getAllDocumentImages();
        int documentImagesCount = getDocumentImagesCount(documentImages);
        
        log.info(String.format("Beginning to cache %d images", documentImagesCount));
        
        int currentImage = 0;
        long lastLogTime = System.currentTimeMillis();
        
        for (Integer metaId : documentImages.keySet()) {
            Map<Integer, ImageDomainObject> images = documentImages.get(metaId);
            
            if (cancelled) {
                return;
            }
            
            for (Integer imageIndex : images.keySet()) {
                if (cancelled) {
                    return;
                }
                
                currentImage++;
                ImageDomainObject imageDomainObject = images.get(imageIndex);
                
                try {
                    cacheImage(metaId, imageIndex, imageDomainObject, config);
                    
                } catch (Exception ex) {
                    log.warn(String.format("Failed to create cache of image, meta_id: %d, image_index: %d", metaId, imageIndex), ex);
                }
                
                long currentTime = System.currentTimeMillis();
                
                if ((currentTime - lastLogTime) >= logInterval) {
                    lastLogTime = currentTime;
                    
                    int progress = (int) ((currentImage / (float) documentImagesCount) * 100.0);
                    
                    log.info(String.format("Progress: %d%%", progress));
                }
            }
        }
    }

    private static int getDocumentImagesCount(Map<Integer, Map<Integer, ImageDomainObject>> documentImages) {
        int total = 0;
        
        for (Map<Integer, ImageDomainObject> images : documentImages.values()) {
            total += images.values().size();
        }
        
        return total;
    }
    
    private static void cacheImage(Integer metaId, Integer imageIndex, ImageDomainObject imageDomainObject, Config config) {
        ImageSource imageSource = imageDomainObject.getSource();
        
        int fileId = 0;
        String fileNo = null;
        String path = null;
        
        if (imageSource instanceof FileDocumentImageSource) {
            FileDocumentDomainObject fileDocument = ((FileDocumentImageSource) imageSource).getFileDocument();
            fileId = fileDocument.getId();
            fileNo = fileDocument.getDefaultFileId();
        } else {
            path = imageDomainObject.getUrlPathRelativeToContextPath();
        }
        
        Format format = imageDomainObject.getFormat();
        
        ImageCacheDomainObject imageCacheObject = ImageHandling.createImageCacheObject(path, null, fileId, fileNo,  
                format, imageDomainObject.getWidth(), imageDomainObject.getHeight(), null, imageDomainObject.getCropRegion(), 
                imageDomainObject.getRotateDirection(), null, null);
        
        SourceFile source = null;
        
        if (path != null) {
            source = ImageHandling.getLocalFile(path);
        } else if (fileId > 0) {
            source = ImageHandling.getFileDocument(fileId, fileNo);
        }
        
        if (source == null) {
            return;
        }
        
        try {
            ImageInfo imageInfo = ImageOp.getImageInfo(config, source.getSourceFile());
            if (imageInfo == null) {
                log.warn(String.format("Failed to create cache of image, meta_id: %d, image_index: %d. Not an image", metaId, imageIndex));

                return;
            }

            if (format == null || !format.isWritable()) {
                format = Format.PNG;
            }

            File cacheFile = ImageCacheManager.storeImage(imageCacheObject, source.getSourceFile());

            if (cacheFile == null) {
                log.warn(String.format("Failed to create cache of image, meta_id: %d, image_index: %d", metaId, imageIndex));
            }
            
        } finally {
            if (source.isDeleteAfterUse()) {
                source.getSourceFile().delete();
            }
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
