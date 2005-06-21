package imcode.util;

import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

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

            imageTagBuffer.append( "<img src=\"" + StringEscapeUtils.escapeHtml( imageUrl ) + "\"" ); // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?

            ImageSize displayImageSize = image.getDisplayImageSize();

            int width = displayImageSize.getWidth();
            int height = displayImageSize.getHeight();
            if ( 0 != width ) {
                imageTagBuffer.append( " width=\"" + width + "\"" );
            }
            if ( 0 != height ) {
                imageTagBuffer.append( " height=\"" + height + "\"" );
            }
            imageTagBuffer.append( " border=\"" + image.getBorder() + "\"" );

            if ( 0 != image.getVerticalSpace() ) {
                imageTagBuffer.append( " vspace=\"" + image.getVerticalSpace() + "\"" );
            }
            if ( 0 != image.getHorizontalSpace() ) {
                imageTagBuffer.append( " hspace=\"" + image.getHorizontalSpace() + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getName() ) ) {
                imageTagBuffer.append( " name=\"" + StringEscapeUtils.escapeHtml( image.getName() ) + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getAlternateText() ) ) {
                imageTagBuffer.append( " alt=\"" + StringEscapeUtils.escapeHtml( image.getAlternateText() ) + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getLowResolutionUrl() ) ) {
                imageTagBuffer.append( " lowsrc=\"" + StringEscapeUtils.escapeHtml( image.getLowResolutionUrl() )
                                       + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getAlign() ) && !"none".equals( image.getAlign() ) ) {
                imageTagBuffer.append( " align=\"" + StringEscapeUtils.escapeHtml( image.getAlign() ) + "\"" );
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
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
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
