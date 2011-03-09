package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.Utility;
import imcode.util.image.Format;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImagePreview extends HttpServlet {
    private static final long serialVersionUID = -5206637712530904625L;

    private static final Log log = LogFactory.getLog(ImagePreview.class);


    protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!hasAccess(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String path = StringUtils.trimToNull(request.getParameter("path"));
        if (path == null) {
            log.error("No path specified");
            send404(response);

            return;
        }

        File root = Imcms.getPath();
        File imageFile = new File(root, path);
        imageFile = imageFile.getCanonicalFile();

        if (!imageFile.getAbsolutePath().startsWith(root.getCanonicalPath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
            
        } else if (!imageFile.exists()) {
            log.error("Source image doesn't exist: " + imageFile);
            send404(response);

            return;
        }

        String formatParam = StringUtils.trimToEmpty(request.getParameter("format")).toLowerCase();
        Format format = Format.findFormatByExtension(formatParam);

        if (format != null && !format.isWritable()) {
            log.error("Format is not writable: " + format);
            send404(response);

            return;
        }

        int width = NumberUtils.toInt(request.getParameter("width"));
		int height = NumberUtils.toInt(request.getParameter("height"));

        if (width < 0) {
            log.error("Invalid width: " + width);
            send404(response);

            return;
        } else if (height < 0) {
            log.error("Invalid height: " + height);
            send404(response);

            return;
        }

        int cropX1 = NumberUtils.toInt(request.getParameter("crop_x1"), -1);
        int cropY1 = NumberUtils.toInt(request.getParameter("crop_y1"), -1);
        int cropX2 = NumberUtils.toInt(request.getParameter("crop_x2"), -1);
        int cropY2 = NumberUtils.toInt(request.getParameter("crop_y2"), -1);

        CropRegion cropRegion = new CropRegion(cropX1, cropY1, cropX2, cropY2);

        int rotateAngle = NumberUtils.toInt(request.getParameter("rangle"));
        RotateDirection rotateDirection = RotateDirection.getByAngleDefaultIfNull(rotateAngle);

        String etag = ImcmsImageUtils.getImageETag(path, imageFile, format, width, height, cropRegion, rotateDirection);
        String ifNoneMatch = request.getHeader("If-None-Match");

        if (etag.equals(ifNoneMatch)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        InputStream inputStream = null;
        File tempFile = File.createTempFile("image_preview", ".tmp");

        try {
            boolean result = ImcmsImageUtils.generateImage(imageFile, tempFile, format,
                    width, height, cropRegion, rotateDirection);
            
            if (result) {
                String contentType = (format != null ? format.getMimeType() : "application/octet-stream");
                response.addHeader("ETag", etag);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType(contentType);
                response.setContentLength((int) tempFile.length());
                
                inputStream = new FileInputStream(tempFile);
                
                IOUtils.copy(inputStream, response.getOutputStream());
                
            } else {
                send404(response);
            }

        } finally {
            IOUtils.closeQuietly(inputStream);

            tempFile.delete();
        }
    }

    private static boolean hasAccess(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser(request);

        return (user != null && !user.isDefaultUser());
    }

    private static void send404(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }
}
