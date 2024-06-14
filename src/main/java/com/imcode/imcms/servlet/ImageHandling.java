package com.imcode.imcms.servlet;

import com.imcode.imcms.domain.dto.ImageCropRegionDTO;
import com.imcode.imcms.domain.dto.ImageData.RotateDirection;
import com.imcode.imcms.model.ImageCropRegion;
import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class ImageHandling extends HttpServlet {
    public static final int IMAGE_MAX_DIMENSION = 4096;
    private static final long serialVersionUID = 6075455980496678862L;
    private static final Logger log = LogManager.getLogger(ImageHandling.class);
    private static final Pattern FILENAME_PATTERN = Pattern.compile("/imagehandling/([^/]+?)/?$");
    private static final Pattern DOT_OR_COLON_PATTERN = Pattern.compile("\\.{2,}|:+");
    private static final Pattern ABSOLUTE_PATH_PATTERN = Pattern.compile("^(\\\\|/)+");
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.9) Gecko/20071025 Firefox/2.0.0.9";
    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient(new ThreadSafeClientConnManager());
    private static final List<String> ALLOWED_PATHS = new ArrayList<>();

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

    private static void writeImageToResponse(String cacheId, File cacheFile, Format format, String desiredFilename,
                                             String path, String etag, HttpServletResponse response) {

        if (etag != null) {
            response.addHeader("ETag", etag);
        }

        String extension = null;

        if (desiredFilename == null) {
            String pathname = cacheId;

            if (path != null) {
                String basename = FilenameUtils.getBaseName(path);

                if (!basename.isEmpty()) {
                    pathname = basename;
                }
            }

            desiredFilename = pathname;

            if (format != null) {
                extension = format.getExtension();
            } else if (path != null) {
                extension = FilenameUtils.getExtension(path);
            }

            if (extension != null && !extension.isEmpty()) {
                desiredFilename += "." + extension;
            }
        }

        final String contentType = (format == null)
                ? ((extension != null && !extension.isEmpty()) ? ("image/" + extension) : "image/png")
                : format.getMimeType();

        response.setContentType(contentType);
        response.setContentLength((int) cacheFile.length());

        // replace " with \"
        desiredFilename = StringUtils.replace(desiredFilename, "\"", "\\\"");

        response.addHeader("Content-Disposition", String.format("inline; filename=\"%s\"", desiredFilename));

        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(cacheFile);
            output = new BufferedOutputStream(response.getOutputStream());

            IOUtils.copy(input, output);
            output.flush();

        } catch (IOException ex) {
            log.fatal("error while write image to response", ex);

        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    static ImageCacheDomainObject createImageCacheObject(String path, String url, int fileId,
                                                         Format format, int width, int height,
                                                         ImageCropRegion cropRegion,
                                                         RotateDirection rotateDirection) {

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

        imageCache.setFormat(format);
        imageCache.setWidth(width);
        imageCache.setHeight(height);
        imageCache.setCropRegion(cropRegion);
        imageCache.setRotateDirection(rotateDirection);
        imageCache.generateId();

        return imageCache;
    }

    static File getLocalFile(String filepath) {
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
                log.fatal("error while getting local file", ex);
            }
        }

        return null;
    }

    private static File retrieveExternalFile(String url) {
        HttpGet fileGet = new HttpGet(url);
        fileGet.addHeader("User-Agent", USER_AGENT);

        try {
            HttpResponse response = HTTP_CLIENT.execute(fileGet);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                drainInput(response.getEntity().getContent());

                return null;
            }

            File file = File.createTempFile("external_file", ".tmp");
            InputStream input = null;
            OutputStream output = null;
            try {
                input = response.getEntity().getContent();
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

    static File getFileDocument(int metaId) {
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

            while (input.read(buffer) > 0) {
            }
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
                log.fatal("error while getting desired filename", ex);
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

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String desiredFilename = getDesiredFilename(request);

        String path = StringUtils.trimToNull(request.getParameter("path"));
        String url = StringUtils.trimToNull(request.getParameter("url"));
        int fileId = NumberUtils.toInt(request.getParameter("file_id"));

        String formatParam = StringUtils.trimToEmpty(request.getParameter("format")).toLowerCase();
        Format format = Format.findFormat(formatParam);

        int width = NumberUtils.toInt(request.getParameter("width"));
        int height = NumberUtils.toInt(request.getParameter("height"));

        // cutting dimensions to prevent generating too big images
        width = Math.min(IMAGE_MAX_DIMENSION, width);
        height = Math.min(IMAGE_MAX_DIMENSION, height);

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

        ImageCropRegion cropRegion = new ImageCropRegionDTO(cropX1, cropY1, cropX2, cropY2);

        int rotateAngle = NumberUtils.toInt(request.getParameter("rangle"));
        RotateDirection rotateDirection = RotateDirection.fromAngle(rotateAngle);

        ImageCacheDomainObject imageCache = createImageCacheObject(path, url, fileId, format, width,
                height, cropRegion, rotateDirection);
        String cacheId = imageCache.getId();

        String etag = null;
        File cacheFile = ImageCacheManager.getCacheFile(imageCache);

        if (cacheFile != null) {
            if (path != null) {
                File imageFile = getLocalFile(path);

                etag = ImcmsImageUtils.getImageETag(path, imageFile, format, width, height, cropRegion, rotateDirection);

                String ifNoneMatch = request.getHeader("If-None-Match");
                if (etag.equals(ifNoneMatch)) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }

            writeImageToResponse(cacheId, cacheFile, format, desiredFilename, path, etag, response);
            return;
        }

        File imageFile;
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

        final String generatedFileName = request.getParameter("generated_file_name");

        if (generatedFileName != null) {
            imageFile = new File(ImcmsImageUtils.imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER + File.separator + generatedFileName);
        }

        cacheFile = ImageCacheManager.storeImage(
                imageCache, imageFile, deleteFile, Optional.ofNullable(generatedFileName).isPresent()
        );

        if (cacheFile == null) {
            sendNotFound(response);
            return;
        }

        Format outputFormat = (format != null ? format : imageInfo.getFormat());
        writeImageToResponse(cacheId, cacheFile, outputFormat, desiredFilename, path, etag, response);
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
