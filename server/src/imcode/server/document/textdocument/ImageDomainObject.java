package imcode.server.document.textdocument;

import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.FileDocumentDomainObject;
import imcode.util.FileInputStreamSource;
import imcode.util.InputStreamSource;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

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
        ImageSize imageSize = new ImageSize( 0, 0 );
        if ( null != source ) {
            try {
                imageSize = source.getImageSize();
            } catch ( IOException ignored ) {}
        }
        return imageSize ;
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

    public abstract static class ImageSource implements Serializable {

        static final int IMAGE_TYPE_ID__NULL = -1;
        public static final int IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH = 0;
        public static final int IMAGE_TYPE_ID__FILE_DOCUMENT = 1;

        private ImageSize cachedImageSize ;
        private Date cachedImageSizeTime;

        abstract InputStreamSource getInputStreamSource();

        abstract String getUrlPathRelativeToContextPath();

        public abstract String toStorageString();

        public abstract int getTypeId();

        public abstract Date getModifiedDatetime();

        ImageSize getImageSize() throws IOException {
            Date modifiedDatetime = getModifiedDatetime();
            if (cachedImageSizeTime == null || modifiedDatetime.after( cachedImageSizeTime )) {
                cachedImageSize = getNonCachedImageSize();
                cachedImageSizeTime = modifiedDatetime ;
            }
            return cachedImageSize ;
        }

        ImageSize getNonCachedImageSize() throws IOException {
            return ImageSize.fromInputStream( getInputStreamSource().getInputStream() );
        }
    }

    public static class FileDocumentImageSource extends ImageSource {

        private DocumentReference fileDocumentReference;

        public FileDocumentImageSource( DocumentReference fileDocumentReference ) {
            this.fileDocumentReference = fileDocumentReference;
            DocumentDomainObject document = fileDocumentReference.getDocument();
            if (!(document instanceof FileDocumentDomainObject)) {
                throw new IllegalArgumentException( "Not a file document: "+document.getId()) ;
            }
        }

        public InputStreamSource getInputStreamSource() {
            return getFileDocument().getDefaultFile().getInputStreamSource();
        }

        public FileDocumentDomainObject getFileDocument() {
            return (FileDocumentDomainObject)fileDocumentReference.getDocument();
        }

        public String getUrlPathRelativeToContextPath() {
            return "/servlet/GetDoc?meta_id=" + getFileDocument().getId();
        }

        public String toStorageString() {
            return "" + getFileDocument().getId();
        }

        public int getTypeId() {
            return ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT;
        }

        public Date getModifiedDatetime() {
            return getFileDocument().getModifiedDatetime() ;
        }
    }

    public static class ImagesPathRelativePathImageSource extends ImageSource {

        private String relativePath;

        public ImagesPathRelativePathImageSource( String relativePath ) {
            this.relativePath = relativePath;
        }

        public InputStreamSource getInputStreamSource() {
            return new FileInputStreamSource( getFile() );
        }

        private File getFile() {
            ImcmsServices service = Imcms.getServices();
            File imageDirectory = service.getConfig().getImagePath();
            File imageFile = new File( imageDirectory, relativePath );
            return imageFile;
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

        public Date getModifiedDatetime() {
            return new Date(getFile().lastModified()) ;
        }
    }

    public class NullImageSource extends ImageSource {

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

        public Date getModifiedDatetime() {
            return null ;
        }
    }
}