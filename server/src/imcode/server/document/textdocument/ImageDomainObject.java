package imcode.server.document.textdocument;

import imcode.util.InputStreamSource;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.FileDocumentDomainObject;
import imcode.util.FileInputStreamSource;
import imcode.util.ImageSize;
import imcode.util.InputStreamSource;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ImageDomainObject implements Serializable {

    private ImageSource source;

    private String name = "";
    private int width;
    private int height;
    private int border;
    private String align = "";
    private String alternateText = "";
    private String lowResolutionUrl = "";
    private int verticalSpace;
    private int horizontalSpace;
    private String target = "";
    private String linkUrl = "";

    public String getName() {
        return name;
    }

    public ImageSize getDisplayImageSize() {
        ImageSize realImageSize = getRealImageSize();

        int wantedWidth = getWidth();
        int wantedHeight = getHeight();
        if ( 0 == wantedWidth && 0 != wantedHeight && 0 != realImageSize.getHeight() ) {
            wantedWidth = (int)( realImageSize.getWidth() * ( (double)wantedHeight / realImageSize.getHeight() ) );
        } else if ( 0 == wantedHeight && 0 != wantedWidth && 0 != realImageSize.getWidth() ) {
            wantedHeight = (int)( realImageSize.getHeight() * ( (double)wantedWidth / realImageSize.getWidth() ) );
        } else if ( 0 == wantedWidth && 0 == wantedHeight ) {
            wantedWidth = realImageSize.getWidth();
            wantedHeight = realImageSize.getHeight();
        }
        return new ImageSize( wantedWidth, wantedHeight );
    }

    public ImageSize getRealImageSize() {
        if ( null == source ) {
            return new ImageSize( 0, 0 );
        }
        try {
            BufferedImage image = ImageIO.read( source.getInputStreamSource().getInputStream() ) ;
            return new ImageSize( image.getWidth(), image.getHeight() );
        } catch ( IOException e ) {
            return new ImageSize( 0, 0 );
        }
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

    public void setSourceAndClearSize( ImageSource source ) {
        setSource( source );
        setWidth( 0 );
        setHeight( 0 );
    }

    public void setSource( ImageSource source ) {
        this.source = source;
    }

    public boolean isEmpty() {
        return null == source;
    }

    public String getUrlPath( String contextPath ) {
        String urlPathRelativeToContextPath = getUrlPathRelativeToContextPath();
        if ( StringUtils.isBlank( urlPathRelativeToContextPath ) ) {
            return "";
        }
        return contextPath + urlPathRelativeToContextPath;
    }

    public String getUrlPathRelativeToContextPath() {
        if ( null == source ) {
            return "";
        }
        return source.getUrlPathRelativeToContextPath();
    }

    public long getSize() {
        if ( null == source ) {
            return 0;
        }
        try {
            return source.getInputStreamSource().getSize();
        } catch ( IOException e ) {
            return 0;
        }
    }

    public ImageSource getSource() {
        if ( null == source ) {
            return new ImageDomainObject.NullImageSource();
        }
        return source;
    }

    public static interface ImageSource {

        int IMAGE_TYPE_ID__NULL = -1;
        int IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH = 0;
        int IMAGE_TYPE_ID__FILE_DOCUMENT = 1;

        InputStreamSource getInputStreamSource();

        String getUrlPathRelativeToContextPath();

        String toStorageString();

        int getTypeId();
    }

    public static class FileDocumentImageSource implements ImageSource {

        private FileDocumentDomainObject fileDocument;

        public FileDocumentImageSource( FileDocumentDomainObject fileDocument ) {
            this.fileDocument = fileDocument;
        }

        public InputStreamSource getInputStreamSource() {
            return fileDocument.getDefaultFile().getInputStreamSource();
        }

        public String getUrlPathRelativeToContextPath() {
            return "/servlet/GetDoc?meta_id=" + fileDocument.getId();
        }

        public String toStorageString() {
            return "" + fileDocument.getId();
        }

        public int getTypeId() {
            return ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT;
        }

        public FileDocumentDomainObject getFileDocument() {
            return fileDocument;
        }
    }

    public static class ImagesPathRelativePathImageSource implements ImageSource {

        private String relativePath;

        public ImagesPathRelativePathImageSource( String relativePath ) {
            this.relativePath = relativePath;
        }

        public InputStreamSource getInputStreamSource() {
            ImcmsServices service = Imcms.getServices();
            File imageDirectory = service.getConfig().getImagePath();
            File imageFile = new File( imageDirectory, relativePath );
            return new FileInputStreamSource( imageFile );
        }

        public String getUrlPathRelativeToContextPath() {
            return Imcms.getServices().getConfig().getImageUrl() + relativePath;

        }

        public String toStorageString() {
            return relativePath;
        }

        public int getTypeId() {
            return ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH;
        }
    }

    public class NullImageSource implements ImageSource {

        public InputStreamSource getInputStreamSource() {
            return null;
        }

        public String getUrlPathRelativeToContextPath() {
            return "";
        }

        public String toStorageString() {
            return "";
        }

        public int getTypeId() {
            return ImageSource.IMAGE_TYPE_ID__NULL;
        }
    }
}