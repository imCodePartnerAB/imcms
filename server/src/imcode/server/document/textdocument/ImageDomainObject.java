/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-maj-03
 * Time: 17:35:24
 */
package imcode.server.document.textdocument;

import imcode.util.ImcmsImageUtils;
import imcode.util.ImageSize;
import imcode.util.ImageParser;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;

import java.io.Serializable;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class ImageDomainObject implements Serializable {
    final static int NON_FILE_DOCUMENT_IMAGE_TYPE_ID = 0;
    public final static int FILE_DOCUMENT_IMAGE_TYPE_ID = 1;

    private int type; // se above, the url is a meta_id if FILE_DOCUMENT_IMAGE_TYPE is the type
    private String url;

    private String name;
    private int width;
    private int height;
    private int border;
    private String align;
    private String alternateText;
    private String lowResolutionUrl;
    private int verticalSpace;
    private int horizontalSpace;
    private String target;
    private String linkUrl;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public ImageSize getDisplayImageSize() {
        ImageSize realImageSize = getRealImageSize();

        int width = getWidth() ;
        int height = getHeight() ;
        if ( 0 == width && 0 != height && 0 != realImageSize.getHeight() ) {
            width = (int)( realImageSize.getWidth() * ( (double)height / realImageSize.getHeight() ) );
        } else if ( 0 == height && 0 != width && 0 != realImageSize.getWidth() ) {
            height = (int)( realImageSize.getHeight() * ( (double)width / realImageSize.getWidth() ) );
        } else if ( 0 == width && 0 == height ) {
            width = realImageSize.getWidth() ;
            height = realImageSize.getHeight() ;
        }
        return new ImageSize( width, height ) ;
    }

    public ImageSize getRealImageSize() {
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        DocumentMapper documentMapper = service.getDocumentMapper();
        File image_path = service.getConfig().getImagePath();
        ImageSize imageSize = new ImageSize( 0, 0 );
        String imageUrl = getUrl();
        if ( StringUtils.isNotBlank( imageUrl ) ) {
            File imageFile = new File( image_path, getUrl() );
            Integer imageFileDocumentId = ImcmsImageUtils.getDocumentIdFromImageUrl( imageUrl );
            if ( null != imageFileDocumentId ) {
                DocumentDomainObject document = documentMapper.getDocument( imageFileDocumentId.intValue() );
                if ( document instanceof FileDocumentDomainObject ) {
                    imageSize = ImcmsImageUtils.getImageSizeFromFileDocument( (FileDocumentDomainObject)document );
                }
            } else if ( imageFile.isFile() ) {
                try {
                    imageSize = new ImageParser().parseImageFile( imageFile );
                } catch (IOException ioe) {
                    // ignored
                }
            }
        }
        return imageSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBorder() {
        return border;
    }

    public String getAlign() {
        return align;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public String getLowResolutionUrl() {
        return lowResolutionUrl;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public String getTarget() {
        return target;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setUrl( String image_ref ) {
        this.url = image_ref;
    }

    public void setName( String image_name ) {
        this.name = image_name;
    }

    public void setWidth( int image_width ) {
        this.width = image_width;
    }

    public void setHeight( int image_height ) {
        this.height = image_height;
    }

    public void setBorder( int image_border ) {
        this.border = image_border;
    }

    public void setAlign( String image_align ) {
        this.align = image_align;
    }

    public void setAlternateText( String alt_text ) {
        this.alternateText = alt_text;
    }

    public void setLowResolutionUrl( String low_scr ) {
        this.lowResolutionUrl = low_scr;
    }

    public void setVerticalSpace( int v_space ) {
        this.verticalSpace = v_space;
    }

    public void setHorizontalSpace( int h_space ) {
        this.horizontalSpace = h_space;
    }

    public void setTarget( String target ) {
        this.target = target;
    }

    public void setLinkUrl( String image_ref_link ) {
        this.linkUrl = image_ref_link;
    }

    public int getType() {
        return type;
    }

    public void setType( int type ) {
        this.type = type;
    }

    public void setUrlAndClearSize( String url ) {
        setUrl( url );
        setWidth( 0 );
        setHeight( 0 );
    }
}