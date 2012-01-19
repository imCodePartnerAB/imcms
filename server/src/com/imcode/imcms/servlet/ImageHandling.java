package com.imcode.imcms.servlet;

import com.imcode.imcms.mapping.DocumentStoringVisitor;
import imcode.server.Imcms;
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import imcode.util.image.Resize;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ImageHandling extends HttpServlet {
	private static final long serialVersionUID = 6075455980496678862L;

	private static final Log log = LogFactory.getLog(ImageHandling.class);
	
	private static final Pattern FILENAME_PATTERN = Pattern.compile("/imagehandling/([^/]+?)/?$");
	private static final Pattern DOT_OR_COLON_PATTERN = Pattern.compile("\\.{2,}|:+");
	private static final Pattern ABSOLUTE_PATH_PATTERN = Pattern.compile("^(\\\\|/)+");
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.9) Gecko/20071025 Firefox/2.0.0.9";
	
	private static final HttpClient HTTP_CLIENT = new HttpClient(new MultiThreadedHttpConnectionManager());
	
	private static final List<String> ALLOWED_PATHS = new ArrayList<String>();
	
	
	static {
		File rootFile = Imcms.getPath();
		String cacheAllowedPaths = Imcms.getServices().getConfig().getImageCacheAllowedPaths();
		
		String[] paths = StringUtils.split(cacheAllowedPaths, ';');
		for (String path : paths) {
			path = path.trim();
			
			if (!StringUtils.isEmpty(path)) {
				try {
					File file = new File(rootFile, path);
					
					ALLOWED_PATHS.add(file.getCanonicalPath());
				} catch (IOException ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}
	}
	
	
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String desiredFilename = getDesiredFilename(request);
		
		String path = StringUtils.trimToNull(request.getParameter("path"));
		String url = StringUtils.trimToNull(request.getParameter("url"));
		int fileId = NumberUtils.toInt(request.getParameter("file_id"));
        String fileNo = StringUtils.trimToNull(request.getParameter("file_no"));
		
        if (fileId <= 0) {
            fileNo = null;
        }
        if (fileNo != null && fileNo.length() > FileDocumentFile.ID_LENGTH) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }
        
        Integer metaId = null;
        Integer no = null;
        try {
            String metaIdStr = StringUtils.trimToNull(request.getParameter("meta_id"));
            metaId = Integer.parseInt(metaIdStr);
            
            String noStr = StringUtils.trimToNull(request.getParameter("no"));
            no = Integer.parseInt(noStr);
        } catch (NumberFormatException ex) {
        }
        
        if (path != null) {
            fileId = 0;
        }
        if (fileId > 0 && metaId == null) {
            metaId = fileId;
        }
        
		String formatParam = StringUtils.trimToEmpty(request.getParameter("format")).toLowerCase();
		Format format = Format.findFormatByExtension(formatParam);
        String resizeParam = StringUtils.trimToNull(request.getParameter("resize"));
        Resize resize = Resize.getByName(resizeParam);
		
		int width = NumberUtils.toInt(request.getParameter("width"));
		int height = NumberUtils.toInt(request.getParameter("height"));
		width = Math.max(width, 0);
		height = Math.max(height, 0);
		
		if ((path == null && url == null && fileId <= 0) || (format != null && !format.isWritable())) {
			sendNotFound(response);
			return;
		}
		
		int cropX1 = NumberUtils.toInt(request.getParameter("crop_x1"), -1);
		int cropY1 = NumberUtils.toInt(request.getParameter("crop_y1"), -1);
		int cropX2 = NumberUtils.toInt(request.getParameter("crop_x2"), -1);
		int cropY2 = NumberUtils.toInt(request.getParameter("crop_y2"), -1);
		
		CropRegion cropRegion = new CropRegion(cropX1, cropY1, cropX2, cropY2);
		
		int rotateAngle = NumberUtils.toInt(request.getParameter("rangle"));
		RotateDirection rotateDirection = RotateDirection.getByAngleDefaultIfNull(rotateAngle);
		
		ImageCacheDomainObject imageCache = createImageCacheObject(path, url, fileId, fileNo, format, width,
                height, resize, cropRegion, rotateDirection, metaId, no);
		String cacheId = imageCache.getId();

        File localImageFile = null;
        if (path != null || fileId > 0) {
            // Special case for local image file, so we could check for file modification.
            SourceFile source;
            if (path != null) {
                source = getLocalFile(path);
            } else {
                source = getFileDocument(fileId, fileNo);
            }

            if (source != null) {
                localImageFile = source.getSourceFile();
            }
        }
        
        String etag = ImcmsImageUtils.getImageETag(path, localImageFile, url, fileId, fileNo, 
                format, width, height, cropRegion, rotateDirection);
        
        // Try to reuse an existing cache file
		File cacheFile = ImageCacheManager.getCacheFile(imageCache);

		if (cacheFile != null) {
            // No need to send the cache file contents if it hasn't changed 
            // based on the Etag header that we send.
            String ifNoneMatch = request.getHeader("If-None-Match");
            if (etag.equals(ifNoneMatch)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            
            boolean useCacheFile = true;
            
            if (path != null || fileId > 0) {
                if (localImageFile == null || localImageFile.lastModified() > cacheFile.lastModified()) {
                    // Don't use the cache file if the local image is missing or has been modified.
                    useCacheFile = false;
                }
            }
            
            if (useCacheFile) {
                writeImageToResponse(cacheId, cacheFile, format, desiredFilename, etag, response);
                return;
            }
		}
        
		// The requested image hasn't been cached. Retrieve and generate a new cache file for it.
        SourceFile source;
		if (path != null) {
            source = getLocalFile(path);
		} else if (fileId > 0) {
            source = getFileDocument(fileId, fileNo);
		} else {
            source = getExternalFile(url);
		}
		
		if (source == null) {
			sendNotFound(response);
			return;
		}
		
        try {
            if (format == null) {
                ImageInfo imageInfo = ImageOp.getImageInfo(Imcms.getServices().getConfig(), source.getSourceFile());
                
                if (imageInfo == null) {
                    log.error("Failed to determine image info for file: " + source.getSourceFile());
                    sendNotFound(response);
                    return;
                }
                
                format = imageInfo.getFormat();
                
                if (!format.isWritable()) {
                    sendNotFound(response);
                    return;
                }
            }
            
            cacheFile = ImageCacheManager.storeImage(imageCache, source.getSourceFile());
            if (cacheFile == null) {
                log.error("Failed to generate/store cache file for image: " + source.getSourceFile());
                sendNotFound(response);
                return;
            }

            writeImageToResponse(cacheId, cacheFile, format, desiredFilename, etag, response);
            
        } finally {
            if (source.isDeleteAfterUse()) {
                source.getSourceFile().delete();
            }
        }
	}
	
	private static void writeImageToResponse(String cacheId, File cacheFile, Format format, String desiredFilename, String etag,
			HttpServletResponse response) {

        if (etag != null) {
            response.addHeader("ETag", etag);
        }

		if (format != null) {
			response.setContentType(format.getMimeType());
		} else {
			response.setContentType("application/octet-stream");
		}
		
		response.setContentLength((int) cacheFile.length());
		
		if (desiredFilename == null) {
			desiredFilename = cacheId;
		}
		
		// replace " with \"
		desiredFilename = StringUtils.replace(desiredFilename, "\"", "\\\"");
		
		response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", desiredFilename));
		
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(cacheFile);
			output = new BufferedOutputStream(response.getOutputStream());
			
			IOUtils.copy(input, output);
			output.flush();
		} catch (IOException ex) {
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}
	
	static ImageCacheDomainObject createImageCacheObject(String path, String url, int fileId, String fileNo, 
			Format format, int width, int height, Resize resize, CropRegion cropRegion, RotateDirection rotateDirection, 
            Integer metaId, Integer no) {
		ImageCacheDomainObject imageCache = new ImageCacheDomainObject();
		
		if (path != null) {
			imageCache.setResource(path);
			imageCache.setType(ImageCacheDomainObject.TYPE_PATH);
		} else if (fileId > 0) {
            String res = Integer.toString(fileId);
            if (fileNo != null) {
                res += "/" + fileNo;
            }
			imageCache.setResource(res);
			imageCache.setType(ImageCacheDomainObject.TYPE_FILE_DOCUMENT);
		} else if (url != null) {
			imageCache.setResource(url);
			imageCache.setType(ImageCacheDomainObject.TYPE_URL);
		} else {
			throw new RuntimeException("path, url or fileId must be valid");
		}
		
		imageCache.setFormat(format);
		imageCache.setWidth(width);
		imageCache.setHeight(height);
        imageCache.setResize(resize);
		imageCache.setCropRegion(cropRegion);
		imageCache.setRotateDirection(rotateDirection);
		imageCache.generateId();
        
        imageCache.setMetaId(metaId);
        imageCache.setNo(no);
        imageCache.setFileNo(fileNo);
		
		return imageCache;
	}
	
	
	
	static SourceFile getLocalFile(String filepath) {
		File root = Imcms.getPath();
		File localFile = new File(root, sanitiseFilepath(filepath));
		
		if (!localFile.exists()) {
			return null;
		}
		
		for (String allowedPath : ALLOWED_PATHS) {
			try {
				if (localFile.getCanonicalPath().startsWith(allowedPath)) {
                    return new SourceFile(localFile, false);
				}
			} catch (IOException ex) {
			}
		}
		
		return null;
	}
	
	private static SourceFile getExternalFile(String url) {
		GetMethod fileGet = new GetMethod(url);
		fileGet.addRequestHeader("User-Agent", USER_AGENT);
		
		try {
			int responseCode = HTTP_CLIENT.executeMethod(fileGet);
			
			if (responseCode != HttpStatus.SC_OK) {
				drainInput(fileGet.getResponseBodyAsStream());
				
				return null;
			}
			
			File file = File.createTempFile("external_file", ".tmp");
			InputStream input = null;
			OutputStream output = null;
			try {
				input = fileGet.getResponseBodyAsStream();
				output = new BufferedOutputStream(new FileOutputStream(file));
				
				IOUtils.copy(input, output);
                output.close();
				
                return new SourceFile(file, true);
				
			} catch (Exception ex) {
				log.warn(ex.getMessage(), ex);
				
				file.delete();
                
			} finally {
				IOUtils.closeQuietly(output);
				IOUtils.closeQuietly(input);
                
			}
            
		} catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
            
		} finally {
			fileGet.releaseConnection();
            
		}
		
		return null;
	}
	
	static SourceFile getFileDocument(int metaId, String fileNo) {
        File file = DocumentStoringVisitor.getFileForFileDocumentFile(metaId, fileNo);
        if (!file.exists()) {
            return null;
        }
        
        return new SourceFile(file, false);
	}
	
	private static void drainInput(InputStream input) throws IOException {
		try {
			byte[] buffer = new byte[4 * 1024];
			
			while (input.read(buffer) > -1) { }
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	private static String getDesiredFilename(HttpServletRequest request) {
		Matcher matcher = FILENAME_PATTERN.matcher(request.getRequestURI());
		if (matcher.find()) {
			try {
				return URLDecoder.decode(matcher.group(1), Imcms.UTF_8_ENCODING);
			} catch (UnsupportedEncodingException ex) {
			}
		}
		
		return null;
	}
	
	private static String sanitiseFilepath(String filepath) {
		Matcher matcher = DOT_OR_COLON_PATTERN.matcher(filepath);
		filepath = matcher.replaceAll(".");
		matcher = ABSOLUTE_PATH_PATTERN.matcher(filepath);
		
		return matcher.replaceFirst("");
	}
	
	private static void sendNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
    
    
    static class SourceFile implements Serializable {
        private static final long serialVersionUID = 6075456980496678862L;
        private final File sourceFile;
        private final boolean deleteAfterUse;

        
        public SourceFile(File sourceFile, boolean deleteAfterUse) {
            this.sourceFile = sourceFile;
            this.deleteAfterUse = deleteAfterUse;
        }

        
        public boolean isDeleteAfterUse() {
            return deleteAfterUse;
        }

        public File getSourceFile() {
            return sourceFile;
        }
    }
}
