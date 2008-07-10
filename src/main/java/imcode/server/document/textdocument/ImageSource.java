package imcode.server.document.textdocument;

import imcode.util.io.InputStreamSource;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.imcode.util.ImageSize;

public abstract class ImageSource implements Serializable {
    public static final int IMAGE_TYPE_ID__NULL = -1;
    public static final int IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH = 0;
    public static final int IMAGE_TYPE_ID__FILE_DOCUMENT = 1;

    private ImageSize cachedImageSize;
    private Date cachedImageSizeTime;

    abstract InputStreamSource getInputStreamSource( );

    public abstract String getUrlPathRelativeToContextPath( );

    public abstract String toStorageString( );

    public abstract int getTypeId( );

    public abstract Date getModifiedDatetime( );

    ImageSize getImageSize( ) throws IOException {
        if ( getInputStreamSource().getSize( ) > 0 ) {
            Date modifiedDatetime = getModifiedDatetime( );
            if ( cachedImageSizeTime == null || modifiedDatetime.after(cachedImageSizeTime) ) {
                cachedImageSize = getNonCachedImageSize();
                cachedImageSizeTime = modifiedDatetime;
            }
            return cachedImageSize;
        }
        return new ImageSize( 0, 0 );
    }

    ImageSize getNonCachedImageSize( ) throws IOException {
        return ImageSize.fromInputStream( getInputStreamSource( ).getInputStream( ) );
    }

    public boolean isEmpty( ) {
        try {
            return getInputStreamSource().getSize() <= 0 ;
        } catch ( IOException e ) {
            return true ;
        }
    }
}

