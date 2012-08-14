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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.imcode.imcms.api.ContentManagementSystem;
import java.util.List;

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
        List<ImageDomainObject> images = Imcms.getServices().getImageCacheMapper().getAllDocumentImages();
        int documentImagesCount = images.size();
        
        log.info(String.format("Beginning to cache %d images", documentImagesCount));
        
        int currentImage = 0;
        long lastLogTime = System.currentTimeMillis();

        for (ImageDomainObject image : images) {
            if (cancelled) {
                return;
            }

            ++currentImage;
            try {
                cacheImage(image);

            } catch (Exception ex) {
                log.warn(getFailureMessage(image), ex);
            }

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastLogTime) >= logInterval) {
                lastLogTime = currentTime;

                int progress = (int) ((currentImage / (float) documentImagesCount) * 100.0);

                log.info(String.format("Progress: %d%%", progress));
            }
        }

        log.info("Done");
    }
    
    private static void cacheImage(ImageDomainObject image) {
        ImageSource imageSource = image.getSource();
        
        int fileId = 0;
        String path = null;
        
        if (imageSource instanceof FileDocumentImageSource) {
            fileId = ((FileDocumentImageSource) imageSource).getFileDocument().getId();
        } else {
            path = image.getUrlPathRelativeToContextPath();
        }
        
        Format format = image.getFormat();
        
        ImageCacheDomainObject imageCacheObject = ImageHandling.createImageCacheObject(path, null, fileId,  
                format, image.getWidth(), image.getHeight(), image.getCropRegion(),
                image.getRotateDirection());
        
        File imageFile = null;
        boolean deleteFile = false;
        
        if (path != null) {
            imageFile = ImageHandling.getLocalFile(path);
        } else if (fileId > 0) {
            imageFile = ImageHandling.getFileDocument(fileId);
            deleteFile = true;
        }
        
        if (imageFile == null) {
            return;
        }
        
        ImageInfo imageInfo = ImageOp.getImageInfo(imageFile);
        if (imageInfo == null) {
            log.warn(getFailureMessage(image));
            
            if (deleteFile) {
                imageFile.delete();
            }
            
            return;
        }
        
        if (format == null || !format.isWritable()) {
            format = Format.PNG;
        }
        
        File cacheFile = ImageCacheManager.storeImage(imageCacheObject, imageFile, deleteFile);
        
        if (cacheFile == null) {
            log.warn(getFailureMessage(image));
        }
    }

    private static String getFailureMessage(ImageDomainObject image) {
        return String.format("Failed to create cache of image: %s", image);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
