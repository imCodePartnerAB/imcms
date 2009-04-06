package imcode.server.document.textdocument;

import com.imcode.util.ImageSize;

import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import imcode.util.io.InputStreamSource;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public abstract class ImageSource implements Serializable {
    public static final int IMAGE_TYPE_ID__NULL = -1;
    public static final int IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH = 0;
    public static final int IMAGE_TYPE_ID__FILE_DOCUMENT = 1;

    private ImageInfo cachedImageInfo;
    private Date cachedImageInfoTime;

    abstract InputStreamSource getInputStreamSource( );

    abstract String getUrlPathRelativeToContextPath( );

    public abstract String toStorageString( );

    public abstract int getTypeId( );

    public abstract Date getModifiedDatetime( );

    ImageSize getImageSize() throws IOException {
    	ImageInfo imageInfo = getImageInfo();
    	if (imageInfo != null) {
    		return new ImageSize(imageInfo.getWidth(), imageInfo.getHeight());
    	}
    	
    	return new ImageSize(0, 0);
    }
    
    ImageInfo getImageInfo() throws IOException {
    	if (getInputStreamSource().getSize() > 0) {
    		Date modifiedDatetime = getModifiedDatetime();
    		if (cachedImageInfoTime == null || modifiedDatetime.after(cachedImageInfoTime)) {
    			cachedImageInfo = getNonCachedImageInfo();
    			cachedImageInfoTime = modifiedDatetime;
    		}
    		
    		return cachedImageInfo;
    	}
    	
    	return null;
    }
    
    ImageInfo getNonCachedImageInfo() throws IOException {
    	return ImageOp.getImageInfo(getInputStreamSource().getInputStream());
    }

    public boolean isEmpty( ) {
        try {
            return getInputStreamSource().getSize() <= 0 ;
        } catch ( IOException e ) {
            return true ;
        }
    }
}

