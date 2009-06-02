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

public class ImcmsImageUtils {

    private ImcmsImageUtils() {
    }

    public static String getImageHtmlTag(Integer metaId, Integer imageIndex, ImageDomainObject image, HttpServletRequest request, Properties attributes) {
        return getImageHtmlTag(metaId, imageIndex, image, request, attributes, false);
    }
    
    public static String getImageHtmlTag(Integer metaId, Integer imageIndex, ImageDomainObject image, HttpServletRequest request, Properties attributes, boolean absoluteUrl) {
        StringBuffer imageTagBuffer = new StringBuffer(96);
        if ( image.getSize() > 0 ) {

            if ( StringUtils.isNotBlank(image.getLinkUrl()) ) {
                imageTagBuffer.append("<a href=\"").append(StringEscapeUtils.escapeHtml(image.getLinkUrl())).append("\"");
                if ( !"".equals(image.getTarget()) ) {
                    imageTagBuffer.append(" target=\"").append(StringEscapeUtils.escapeHtml(image.getTarget())).append("\"");
                }
                imageTagBuffer.append('>');
            }
            
            String urlEscapedImageUrl = getImageUrl(metaId, imageIndex, image, request.getContextPath());
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

            int width = image.getWidth();
            int height = image.getHeight();
            
            if ( 0 != width ) {
                imageTagBuffer.append(" width=\"").append(width).append("\"");
                styleBuffer.append(" width: ").append(width).append("px;");
            }
            if ( 0 != height ) {
                imageTagBuffer.append(" height=\"").append(height).append("\"");
                styleBuffer.append(" height: ").append(height).append("px;");
            }

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
    
    public static String getImageUrl(Integer metaId, Integer imageIndex, ImageDomainObject image, String contextPath) {
    	StringBuilder builder = new StringBuilder();
        builder.append(contextPath);
        builder.append("/imagehandling?");
        
        if (image.getSource() instanceof FileDocumentImageSource) {
        	FileDocumentImageSource source = (FileDocumentImageSource) image.getSource();
        	builder.append("file_id=");
        	builder.append(source.getFileDocument().getId());
        } else {
        	builder.append("path=");
        	builder.append(Utility.encodeUrl(image.getUrlPathRelativeToContextPath()));
        }
        
        if (metaId != null && imageIndex != null) {
        	builder.append("&meta_id=");
        	builder.append(metaId);
        	builder.append("&image_index=");
        	builder.append(imageIndex);
        }
        
        builder.append("&width=");
        builder.append(image.getWidth());
        builder.append("&height=");
        builder.append(image.getHeight());
        
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
        
        builder.append("&rangle=");
        builder.append(image.getRotateDirection().getAngle());
        
        return builder.toString();
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
