package imcode.util;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageArchiveImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.image.Filter;
import imcode.util.image.Format;
import imcode.util.image.ImageOp;
import imcode.util.image.Resize;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.InputStreamSource;
import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImcmsImageUtils {
    private static final Log log = LogFactory.getLog(ImcmsImageUtils.class);
    
    // Set of file paths of images being currently generated.
    private static final Set<String> IMAGES_BEING_GENERATED = Collections.synchronizedSet(new HashSet<String>());
    

    private ImcmsImageUtils() {
    }

    public static String getImageHtmlTag(Integer metaId, ImageDomainObject image, HttpServletRequest request, Properties attributes) {
        return getImageHtmlTag(metaId, image, request, attributes, false);
    }

    public static String getImageHtmlTag(Integer metaId, ImageDomainObject image, HttpServletRequest request, Properties attributes, boolean absoluteUrl) {
        return getImageHtmlTag(metaId, image, request, attributes, absoluteUrl, false);
    }

    public static String getImagePreviewHtmlTag(ImageDomainObject image, HttpServletRequest request, Properties attributes) {
        return getImageHtmlTag(null, image, request, attributes, false, true);
    }
    
    private static String getImageHtmlTag(Integer metaId, ImageDomainObject image, HttpServletRequest request, Properties attributes,
            boolean absoluteUrl, boolean forPreview) {
        
        StringBuffer imageTagBuffer = new StringBuffer(96);
        if ( image.getSize() > 0 ) {

            if ( StringUtils.isNotBlank(image.getLinkUrl()) ) {
                imageTagBuffer.append("<a href=\"").append(StringEscapeUtils.escapeHtml(image.getLinkUrl())).append("\"");
                if ( !"".equals(image.getTarget()) ) {
                    imageTagBuffer.append(" target=\"").append(StringEscapeUtils.escapeHtml(image.getTarget())).append("\"");
                }
                imageTagBuffer.append('>');
            }
            
            String urlEscapedImageUrl;
            if (forPreview) {
                urlEscapedImageUrl = getImagePreviewUrl(image, request.getContextPath());
            } else {
                urlEscapedImageUrl = getImageUrl(metaId, image, request.getContextPath());
            }

            if (absoluteUrl) {
                StringBuffer requestURL = request.getRequestURL();
                urlEscapedImageUrl = requestURL.substring(0,StringUtils.ordinalIndexOf(requestURL.toString(), "/", 3))+urlEscapedImageUrl;
            }
            
            imageTagBuffer.append("<img src=\"").append(StringEscapeUtils.escapeHtml(urlEscapedImageUrl)).append("\"");

            imageTagBuffer.append(" alt=\"").append(StringEscapeUtils.escapeHtml(image.getAlternateText())).append("\"");
            imageTagBuffer.append(" title=\"").append(StringEscapeUtils.escapeHtml(image.getAlternateText())).append("\"");

            String id = image.getName();
            String idAttribute = attributes.getProperty("id");
            if ( StringUtils.isNotBlank(idAttribute) ) {
                id = idAttribute;
            }
            if ( StringUtils.isNotBlank(id) ) {
                imageTagBuffer.append(" id=\"").append(StringEscapeUtils.escapeHtml(id)).append("\"");
            }

            String classAttribute = attributes.getProperty("class");
            if ( null != classAttribute ) {
                imageTagBuffer.append(" class=\"").append(StringEscapeUtils.escapeHtml(classAttribute)).append("\"");
            }

            String usemapAttribute = attributes.getProperty("usemap");
            if ( null != usemapAttribute ) {
                imageTagBuffer.append(" usemap=\"").append(StringEscapeUtils.escapeHtml(usemapAttribute)).append("\"");
            }

            StringBuilder styleBuffer = new StringBuilder();

            styleBuffer.append("border-width: ").append(image.getBorder()).append("px;");

            styleBuffer.append(" margin: ")
                    .append(image.getVerticalSpace()).append("px ")
                    .append(image.getHorizontalSpace()).append("px;");

            if ( StringUtils.isNotBlank(image.getAlign()) && "left".equals(image.getAlign()) ) {
                styleBuffer.append(" align: ").append(StringEscapeUtils.escapeHtml(image.getAlign())).append(";");
            }
            if ( StringUtils.isNotBlank(image.getAlign()) && "right".equals(image.getAlign()) ) {
                styleBuffer.append(" align: ").append(StringEscapeUtils.escapeHtml(image.getAlign())).append(";");
            }
            if ( StringUtils.isNotBlank(image.getAlign()) && !"none".equals(image.getAlign()) ) {
                styleBuffer.append(" vertical-align: ").append(StringEscapeUtils.escapeHtml(image.getAlign())).append(";");
            }

            String styleAttribute = attributes.getProperty("style");
            if ( null != styleAttribute ) {
                styleBuffer.append(" ").append(styleAttribute);
            }

            imageTagBuffer.append(" style=\"").append(StringEscapeUtils.escapeHtml(styleBuffer.toString())).append("\"");

            imageTagBuffer.append(" />");
            if ( StringUtils.isNotBlank(image.getLinkUrl()) ) {
                imageTagBuffer.append("</a>");
            }
        }
        return imageTagBuffer.toString();
    }
    
    public static String getImageUrl(Integer metaId, ImageDomainObject image, String contextPath) {
        return getImageUrl(metaId, image, contextPath, false);
    }
    
    public static String getImageUrl(Integer metaId, ImageDomainObject image, String contextPath, boolean includeQueryParams) {
        String generatedFilename = image.getGeneratedFilename();
        
        if (generatedFilename == null) {
            return getImageHandlingUrl(metaId, image, contextPath);
        }
        
        File generatedFile = image.getGeneratedFile();
        
        if (!generatedFile.exists()) {
            generateImage(image, false);
            
        } else if (isImageModified(image, generatedFile)) {
            generateImage(image, true);
            
        }
        
        String url = image.getGeneratedUrlPath(contextPath);

        if (includeQueryParams) {
            url += getImageQueryString(metaId, image, false);
        }

        return url;
    }

    public static String getImageHandlingUrl(Integer metaId, ImageDomainObject image, String contextPath) {
        
        return contextPath + "/imagehandling" + getImageQueryString(metaId, image, false);
    }

    public static String getImagePreviewUrl(ImageDomainObject image, String contextPath) {

        return contextPath + "/servlet/ImagePreview" + getImageQueryString(null, image, true);
    }
    
    private static String getImageQueryString(Integer metaId, ImageDomainObject image, boolean forPreview) {
        StringBuilder builder = new StringBuilder("?");
        
        if (!forPreview && image.getSource() instanceof FileDocumentImageSource) {
            FileDocumentImageSource source = (FileDocumentImageSource) image.getSource();
            FileDocumentDomainObject fileDocument = source.getFileDocument();
        	builder.append("file_id=");
        	builder.append(fileDocument.getId());
            builder.append("&file_no=");
            builder.append(fileDocument.getDefaultFileId());
        } else {
        	builder.append("path=");
        	builder.append(Utility.encodeUrl(image.getUrlPathRelativeToContextPath()));
        }
        
        if (!forPreview) {
            builder.append("&meta_id=");
            builder.append(metaId);
            
            Integer imageIndex = image.getImageIndex();
            if (imageIndex != null) {
                builder.append("&no=");
                builder.append(imageIndex);
            }
        }
        
        if (image.getWidth() > 0) {
            builder.append("&width=");
            builder.append(image.getWidth());
        }
        if (image.getHeight() > 0) {
            builder.append("&height=");
            builder.append(image.getHeight());
        }
        
        if (image.getFormat() != null) {
        	builder.append("&format=");
        	builder.append(image.getFormat().getExtension());
        }
        
        CropRegion region = image.getCropRegion();
        if (region.isValid()) {
        	builder.append("&crop_x1=");
            builder.append(region.getCropX1());
            builder.append("&crop_y1=");
            builder.append(region.getCropY1());
            builder.append("&crop_x2=");
            builder.append(region.getCropX2());
            builder.append("&crop_y2=");
            builder.append(region.getCropY2());
        }
        
        RotateDirection rotateDir = image.getRotateDirection();
        if (!rotateDir.isDefault()) {
            builder.append("&rangle=");
            builder.append(rotateDir.getAngle());
        }

        if (!forPreview && image.getGeneratedFilename() != null) {
            builder.append("&gen_file=");
            builder.append(image.getGeneratedFilename());
        }
        
        if (image.getResize() != null) {
            builder.append("&resize=");
            builder.append(image.getResize().name().toLowerCase());
        }

        return builder.toString();
    }

    private static boolean isImageModified(ImageDomainObject image, File generatedFile) {
        Date sourceModDate = image.getSource().getModifiedDatetime();
        
        if (sourceModDate == null) {
            return true;
        }
        
        long lastModified = generatedFile.lastModified();
        if (lastModified == 0L) {
            return true;
        }
        Date generatedModDate = new Date(lastModified);
        
        return sourceModDate.after(generatedModDate);
    }
    
    public static void generateImage(ImageDomainObject image, boolean overwrite) {
        File genFile = image.getGeneratedFile();

        if (!overwrite && genFile.exists()) {
            return;
        }

        ImageSource source = image.getSource();

        if (source instanceof NullImageSource) {
            if (overwrite && genFile.exists()) {
                genFile.delete();
            }
            return;
        }
        
        InputStream input = null;
        OutputStream output = null;
        File tempFile = null;

        try {
            File imagePath = Imcms.getServices().getConfig().getImagePath();
            String imagePathCanon = imagePath.getCanonicalPath();
            String genFileCanon = genFile.getCanonicalPath();

            if (!genFileCanon.startsWith(imagePathCanon)) {
                return;
            }

            File parentFile = genFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdir();
            }

            File inputFile;
            
            InputStreamSource inputSource = source.getInputStreamSource();
            if (inputSource instanceof FileInputStreamSource) {
                inputFile = ((FileInputStreamSource) inputSource).getFile();
                
            } else {
                tempFile = File.createTempFile("genimg", null);
                inputFile = tempFile;

                input = source.getInputStreamSource().getInputStream();
                output = new BufferedOutputStream(new FileOutputStream(tempFile));

                IOUtils.copy(input, output);
                IOUtils.closeQuietly(output);
                
            }
            

            generateImage(inputFile, genFile, image.getFormat(), image.getWidth(), image.getHeight(), image.getResize(), 
                    image.getCropRegion(), image.getRotateDirection());

        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            
            genFile.delete();

        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);

            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    public static boolean generateImage(File imageFile, File destFile, Format format, int width, int height, 
            Resize resize, CropRegion cropRegion, RotateDirection rotateDir) {

        String destPath;
        try {
            destPath = destFile.getCanonicalPath();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        if (!IMAGES_BEING_GENERATED.add(destPath)) {
            return true;
        }
        
        try {
            ImageOp operation = new ImageOp(Imcms.getServices().getConfig()).input(imageFile);


            if (!rotateDir.isDefault()) {
                operation.rotate(rotateDir.getAngle());
            }

            if (cropRegion.isValid()) {
                int cropWidth = cropRegion.getWidth();
                int cropHeight = cropRegion.getHeight();

                operation.crop(cropRegion.getCropX1(), cropRegion.getCropY1(), cropWidth, cropHeight);
            }

            if (width > 0 || height > 0) {
                Integer w = (width > 0 ? width : null);
                Integer h = (height > 0 ? height : null);
                
                if (resize == null) {
                    resize = (width > 0 && height > 0 ? Resize.FORCE : Resize.DEFAULT);
                }

                operation.filter(Filter.LANCZOS);
                operation.resize(w, h, resize);
            }

            if (format != null) {
                operation.outputFormat(format);
            }

            return operation.processToFile(destFile);
            
        } finally {
            IMAGES_BEING_GENERATED.remove(destPath);
            
        }
    }

    public static String getImageETag(String path, File imageFile, String url, int fileId, String fileNo, 
            Format format, int width, int height, CropRegion cropRegion, 
            RotateDirection rotateDirection) {

        StringBuilder builder = new StringBuilder();
        builder.append(path);
        if (imageFile != null) {
            builder.append(imageFile.length());
            builder.append(imageFile.lastModified());
        }
        builder.append(width);
        builder.append(height);
        builder.append(rotateDirection.name());

        if (format != null) {
            builder.append(format.getOrdinal());
        }
        if (cropRegion != null) {
            builder.append(cropRegion.getCropX1());
            builder.append(cropRegion.getCropY1());
            builder.append(cropRegion.getCropX2());
            builder.append(cropRegion.getCropY2());
        }
        
        if (url != null) {
            builder.append(url);
        }
        if (fileId > 0) {
            builder.append(fileId);
        }
        if (fileNo != null) {
            builder.append(fileNo);
        }

        return "W/\"" + DigestUtils.md5Hex(builder.toString()) + "\"";
    }

    public static ImageSource createImageSourceFromString(String imageUrl) {
        ImageSource imageSource = new NullImageSource();
        if ( StringUtils.isNotBlank(imageUrl) ) {
            ImcmsServices services = Imcms.getServices();
            DocumentMapper documentMapper = services.getDocumentMapper();
            String documentIdString = ImcmsSetupFilter.getDocumentIdString(services, imageUrl);
            DocumentDomainObject document = documentMapper.getDocument(documentIdString);
            if ( document instanceof FileDocumentDomainObject ) {
                imageSource = new FileDocumentImageSource(documentMapper.getDocumentReference(document));
            } else {
            	String imageArchiveImageUrl = ImageArchiveImageSource.getImagesUrlPath();
            	String imagesPath = ImagesPathRelativePathImageSource.getImagesUrlPath();
            	
            	if (imageUrl.startsWith(imageArchiveImageUrl)) {
            		imageUrl = imageUrl.substring(imageArchiveImageUrl.length());
            		
            		imageSource = new ImageArchiveImageSource(imageUrl);
            	} else {
            		if (imageUrl.startsWith(imagesPath)) {
            			imageUrl = imageUrl.substring(imagesPath.length());
            		}
            		
            		imageSource = new ImagesPathRelativePathImageSource(imageUrl);
            	}
            }
        }
        return imageSource;
    }
}
