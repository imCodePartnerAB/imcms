package imcode.util;

import com.imcode.imcms.servlet.GetDoc;
import imcode.server.ApplicationServer;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;

import java.io.IOException;
import java.io.InputStream;

public class ImcmsImageUtils {

    public static Integer getDocumentIdFromImageUrl( String imageUrl ) {
        Integer documentId = null;
        Perl5Util perl5util = new Perl5Util();
        if ( perl5util.match( "/GetDoc\\?meta_id=(\\d+)/", imageUrl ) ) {
            documentId = Integer.valueOf( perl5util.group( 1 ) );
        }
        return documentId;
    }

    public static String getFileIdFromImageUrl( String imageUrl ) {
        String fileId = "" ;
        Perl5Util perl5util = new Perl5Util();
        if ( perl5util.match( "/GetDoc\\?.*\\b"+GetDoc.REQUEST_PARAMETER__FILE_ID+"=([^&]+)/", imageUrl )) {
            fileId = perl5util.group( 1 ) ;
        }
        return fileId;
    }

    public static ImageSize getImageSizeFromFileDocument( FileDocumentDomainObject imageFileDocument,
                                                          String fileId ) {
        ImageSize imageSize;
        try {
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = imageFileDocument.getFileOrDefault( fileId );
            InputStream imageFileDocumentInputStream = fileDocumentFile.getInputStreamSource().getInputStream();
            imageSize = new ImageParser().parseImageStream( imageFileDocumentInputStream, fileDocumentFile.getFilename() );
        } catch ( IllegalArgumentException iae ) {
            imageSize = new ImageSize( 0, 0 );
        } catch ( IOException ioe ) {
            imageSize = new ImageSize( 0, 0 );
        }
        return imageSize;
    }

    public static String getImageHtmlTag( ImageDomainObject image ) {
        StringBuffer imageTagBuffer = new StringBuffer( 96 );
        if ( !"".equals( image.getUrl() ) ) {

            if ( StringUtils.isNotBlank( image.getLinkUrl() ) ) {
                imageTagBuffer.append( "<a href=\"" ).append( StringEscapeUtils.escapeHtml( image.getLinkUrl() ) ).append( "\"" );
                if ( !"".equals( image.getTarget() ) ) {
                    imageTagBuffer.append( " target=\"" ).append( StringEscapeUtils.escapeHtml( image.getTarget() ) ).append( "\"" );
                }
                imageTagBuffer.append( '>' );
            }

            String imageUrl = ApplicationServer.getIMCServiceInterface().getConfig().getImageUrl() + image.getUrl();

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

}
