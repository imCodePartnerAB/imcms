package imcode.util;

import com.imcode.imcms.mapping.DefaultDocumentMapper;
import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

public class ImcmsImageUtils {

    private ImcmsImageUtils() {
    }

    public static String getImageHtmlTag( ImageDomainObject image, HttpServletRequest request ) {
        StringBuffer imageTagBuffer = new StringBuffer( 96 );
        if ( image.getSize() > 0) {

            if ( StringUtils.isNotBlank( image.getLinkUrl() ) ) {
                imageTagBuffer.append( "<a href=\"" ).append( StringEscapeUtils.escapeHtml( image.getLinkUrl() ) ).append( "\"" );
                if ( !"".equals( image.getTarget() ) ) {
                    imageTagBuffer.append( " target=\"" ).append( StringEscapeUtils.escapeHtml( image.getTarget() ) ).append( "\"" );
                }
                imageTagBuffer.append( '>' );
            }

            String imageUrl = request.getContextPath()+image.getUrlPathRelativeToContextPath();

            imageTagBuffer.append("<img src=\"").append(StringEscapeUtils.escapeHtml(URLEncoder.encode(imageUrl).replaceAll("%2F", "/"))).append("\"");

            ImageSize displayImageSize = image.getDisplayImageSize();

            int width = displayImageSize.getWidth();
            int height = displayImageSize.getHeight();
            if ( 0 != width ) {
                imageTagBuffer.append(" width=\"").append(width).append("\"");
            }
            if ( 0 != height ) {
                imageTagBuffer.append(" height=\"").append(height).append("\"");
            }
            imageTagBuffer.append(" border=\"").append(image.getBorder()).append("\"");

            if ( 0 != image.getVerticalSpace() ) {
                imageTagBuffer.append(" vspace=\"").append(image.getVerticalSpace()).append("\"");
            }
            if ( 0 != image.getHorizontalSpace() ) {
                imageTagBuffer.append(" hspace=\"").append(image.getHorizontalSpace()).append("\"");
            }
            if ( StringUtils.isNotBlank( image.getName() ) ) {
                imageTagBuffer.append(" name=\"").append(StringEscapeUtils.escapeHtml(image.getName())).append("\"");
            }
            if ( StringUtils.isNotBlank( image.getAlternateText() ) ) {
                imageTagBuffer.append(" alt=\"").append(StringEscapeUtils.escapeHtml(image.getAlternateText())).append("\"");
            }
            if ( StringUtils.isNotBlank( image.getLowResolutionUrl() ) ) {
                imageTagBuffer.append(" lowsrc=\"").append(StringEscapeUtils.escapeHtml(URLEncoder.encode(imageUrl).replaceAll("%2F", "/"))).append("\"");
            }
            if ( StringUtils.isNotBlank( image.getAlign() ) && !"none".equals( image.getAlign() ) ) {
                imageTagBuffer.append(" align=\"").append(StringEscapeUtils.escapeHtml(image.getAlign())).append("\"");
            }
            imageTagBuffer.append( ">" );
            if ( StringUtils.isNotBlank( image.getLinkUrl() ) ) {
                imageTagBuffer.append( "</a>" );
            }
        }
        return imageTagBuffer.toString();
    }

    public static ImageSource createImageSourceFromString( String imageUrl ) {
        ImageSource imageSource = null;

        try {
            DefaultDocumentMapper documentMapper = Imcms.getServices().getDefaultDocumentMapper();
            DocumentDomainObject document = documentMapper.getDocument( Integer.parseInt( imageUrl ) );
            if ( document instanceof FileDocumentDomainObject ) {
                imageSource = new FileDocumentImageSource( documentMapper.getDocumentReference( document ) );
            }
        } catch ( NumberFormatException nfe ) {
            imageSource = new ImagesPathRelativePathImageSource( imageUrl );
        }
        return imageSource;
    }
}
