package imcode.util;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.oro.text.perl.Perl5Util;

public class ImcmsImageUtils {

    public static ImageSize getImageSize( ImageDomainObject image ) throws IOException {
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        DocumentMapper documentMapper = service.getDocumentMapper();
        File image_path = service.getConfig().getImagePath();
        ImageSize imageSize = new ImageSize( 0, 0 );
        String imageUrl = image.getUrl();
        if ( StringUtils.isNotBlank( imageUrl ) ) {
            File imageFile = new File( image_path, image.getUrl() );
            Integer imageFileDocumentId = getDocumentIdFromImageUrl( imageUrl );
            if ( null != imageFileDocumentId ) {
                DocumentDomainObject document = documentMapper.getDocument( imageFileDocumentId.intValue() );
                if ( document instanceof FileDocumentDomainObject ) {
                    imageSize = getImageDataFromFileDocument( (FileDocumentDomainObject)document );
                }
            } else if ( imageFile.isFile() ) {
                imageSize = new ImageParser().parseImageFile( imageFile );
            }
        }
        return imageSize;
    }

    public static Integer getDocumentIdFromImageUrl( String imageUrl ) {
        Integer documentId = null;
        Perl5Util perl5util = new Perl5Util();
        if ( perl5util.match( "/GetDoc\\?meta_id=(\\d+)/", imageUrl ) ) {
            documentId = Integer.valueOf( perl5util.group( 1 ) );
        }
        return documentId;
    }

    public static ImageSize getImageDataFromFileDocument( FileDocumentDomainObject imageFileDocument ) {
        ImageSize imageSize;
        try {
            InputStream imageFileDocumentInputStream = imageFileDocument.getInputStreamSource().getInputStream();
            imageSize = new ImageParser().parseImageStream( imageFileDocumentInputStream, imageFileDocument.getFilename() );
        } catch ( IllegalArgumentException iae ) {
            imageSize = new ImageSize( 0, 0 );
        } catch ( IOException ioe ) {
            imageSize = new ImageSize( 0, 0 );
        }
        return imageSize;
    }

    public static String getImageTag( ImageDomainObject image ) {
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

            ImageSize imageSize = new ImageSize( 0, 0 );
            try {
                imageSize = getImageSize( image );
            } catch ( IOException e ) {
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if ( 0 == width && 0 != height && 0 != imageSize.getHeight() ) {
                width = (int)( imageSize.getWidth() * ( (double)height / imageSize.getHeight() ) );
            } else if ( 0 == height && 0 != width && 0 != imageSize.getWidth() ) {
                height = (int)( imageSize.getHeight() * ( (double)width / imageSize.getWidth() ) );
            } else if ( 0 == width && 0 == height ) {
                width = imageSize.getWidth() ;
                height = imageSize.getHeight() ;
            }

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
