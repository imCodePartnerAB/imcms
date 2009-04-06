package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
		int metaId = NumberUtils.toInt(request.getParameter("meta_id"));
		int imageIndex = NumberUtils.toInt(request.getParameter("image_index"));
		
		String formatParam = StringUtils.trimToEmpty(request.getParameter("format")).toLowerCase();
		Format format = Format.findFormatByExtension(formatParam);
		
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
		 
		ImageCacheDomainObject imageCache = createImageCacheObject(path, url, fileId, metaId, imageIndex, 
				format, width, height, cropRegion);
		String cacheId = imageCache.getId();
		
		File cacheFile = ImageCacheManager.getCacheFile(imageCache);
		if (cacheFile != null) {
			
			writeImageToResponse(cacheId, cacheFile, format, desiredFilename, response); 
			return;
		}
		
		if (!canGenerateImage(metaId, request)) {
			sendNotFound(response);
			return;
		}
		
		File imageFile = null;
		boolean deleteFile = false;
		if (path != null) {
			imageFile = getLocalFile(path);
		} else if (fileId > 0) {
			imageFile = getFileDocument(fileId);
			deleteFile = true;
		} else {
			imageFile = retrieveExternalFile(url);
			deleteFile = true;
		}
		
		if (imageFile == null) {
			sendNotFound(response);
			return;
		}
		
		ImageInfo imageInfo = ImageOp.getImageInfo(imageFile);
		if (imageInfo == null || (format == null && !imageInfo.getFormat().isWritable())) {
			if (deleteFile) {
				imageFile.delete();
			}
			
			sendNotFound(response);
			return;
		}
		
		cacheFile = ImageCacheManager.storeImage(imageCache, imageFile, deleteFile);
		if (cacheFile == null) {
			sendNotFound(response);
			return;
		}
		
		Format outputFormat = (format != null ? format : imageInfo.getFormat());
		writeImageToResponse(cacheId, cacheFile, outputFormat, desiredFilename, response);
	}
	
	private static boolean canGenerateImage(int metaId, HttpServletRequest request) {
		UserDomainObject user = Utility.getLoggedOnUser(request);
		if (user == null) {
			return false;
		}
		
		if (metaId > 0) {
			DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(metaId);
			if (document == null || !(document instanceof TextDocumentDomainObject)) {
				return false;
			}
			TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(document);
			
			return permissionSet.getEditImages();
		}
		
		return true;
	}
	
	private static void writeImageToResponse(String cacheId, File cacheFile, Format format, String desiredFilename, 
			HttpServletResponse response) {
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
	
	private static ImageCacheDomainObject createImageCacheObject(String path, String url, int fileId, int metaId, int imageIndex, 
			Format format, int width, int height, CropRegion cropRegion) {
		ImageCacheDomainObject imageCache = new ImageCacheDomainObject();
		
		if (path != null) {
			imageCache.setResource(path);
			imageCache.setType(ImageCacheDomainObject.TYPE_PATH);
		} else if (fileId > 0) {
			imageCache.setResource(Integer.toString(fileId));
			imageCache.setType(ImageCacheDomainObject.TYPE_FILE_DOCUMENT);
		} else if (url != null) {
			imageCache.setResource(url);
			imageCache.setType(ImageCacheDomainObject.TYPE_URL);
		} else {
			throw new RuntimeException("path, url or fileId must be valid");
		}
		
		if (metaId > 0) {
			imageCache.setMetaId(metaId);
			imageCache.setImageIndex(imageIndex);
		}
		
		imageCache.setFormat(format);
		imageCache.setWidth(width);
		imageCache.setHeight(height);
		imageCache.setCropRegion(cropRegion);
		imageCache.generateId();
		
		return imageCache;
	}
	
	
	
	private static File getLocalFile(String filepath) {
		File root = Imcms.getPath();
		File localFile = new File(root, sanitiseFilepath(filepath));
		
		if (!localFile.exists()) {
			return null;
		}
		
		for (String allowedPath : ALLOWED_PATHS) {
			try {
				if (localFile.getCanonicalPath().startsWith(allowedPath)) {
					return localFile;
				}
			} catch (IOException ex) {
			}
		}
		
		return null;
	}
	
	private static File retrieveExternalFile(String url) {
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
				
				return file;
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
	
	private static File getFileDocument(int metaId) {
		DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(metaId);
		
		if (document == null || !(document instanceof FileDocumentDomainObject)) {
			return null;
		}
		
		FileDocumentDomainObject fileDocument = (FileDocumentDomainObject) document;
		FileDocumentFile documentFile = fileDocument.getDefaultFile();
		if (documentFile == null) {
			return null;
		}
		
		File file = null;
		InputStream input = null;
		OutputStream output = null;
		try {
			file = File.createTempFile("doc_file", ".tmp");
			output = new BufferedOutputStream(new FileOutputStream(file));
			input = documentFile.getInputStreamSource().getInputStream();
			
			IOUtils.copy(input, output);
			
			return file;
		} catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			
			if (file != null) {
				file.delete();
			}
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}
		
		return null;
	}
	
	private static void drainInput(InputStream input) throws IOException {
		try {
			byte[] buffer = new byte[4 * 1024];
			
			while (input.read(buffer) > 0) { };
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
}
