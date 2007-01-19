package imcode.util;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class ImcmsImageUtils {

    private ImcmsImageUtils() {
    }

    public static String getImageHtmlTag(ImageDomainObject image, HttpServletRequest request, Properties attributes) {
        StringBuffer imageTagBuffer = new StringBuffer(96);
        if ( image.getSize() > 0 ) {

            if ( StringUtils.isNotBlank(image.getLinkUrl()) ) {
                imageTagBuffer.append("<a href=\"").append(StringEscapeUtils.escapeHtml(image.getLinkUrl())).append("\"");
                if ( !"".equals(image.getTarget()) ) {
                    imageTagBuffer.append(" target=\"").append(StringEscapeUtils.escapeHtml(image.getTarget())).append("\"");
                }
                imageTagBuffer.append('>');
            }

            String imageUrl = request.getContextPath() + image.getUrlPathRelativeToContextPath();

            imageTagBuffer.append("<img src=\"").append(StringEscapeUtils.escapeHtml(Utility.escapeUrl(imageUrl))).append("\"");

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

            ImageSize displayImageSize = image.getDisplayImageSize();
            int width = displayImageSize.getWidth();
            int height = displayImageSize.getHeight();
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
                    .append(image.getHorizontalSpace()).append("px ");

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

    public static ImageSource createImageSourceFromString(String imageUrl) {
        ImageSource imageSource = new NullImageSource();
        if ( StringUtils.isNotBlank(imageUrl) ) {
            try {
                DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
                DocumentDomainObject document = documentMapper.getDocument(Integer.parseInt(imageUrl));
                if ( document instanceof FileDocumentDomainObject ) {
                    imageSource = new FileDocumentImageSource(documentMapper.getDocumentReference(document));
                }
            } catch ( NumberFormatException nfe ) {
                String imagesPath = ImagesPathRelativePathImageSource.getImagesUrlPath();
                if (imageUrl.startsWith(imagesPath)) {
                    imageUrl = imageUrl.substring(imagesPath.length());
                }
                imageSource = new ImagesPathRelativePathImageSource(imageUrl);
            }
        }
        return imageSource;
    }
}
