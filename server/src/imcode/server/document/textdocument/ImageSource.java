package imcode.server.document.textdocument;

import imcode.server.Imcms;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import imcode.util.io.InputStreamSource;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.imcode.util.ImageSize;
import imcode.util.io.FileInputStreamSource;
import java.io.File;

public abstract class ImageSource implements Serializable {
    public static final int IMAGE_TYPE_ID__NULL = -1;
    public static final int IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH = 0;
    public static final int IMAGE_TYPE_ID__FILE_DOCUMENT = 1;
    public static final int IMAGE_TYPE_ID__IMAGE_ARCHIVE = 2;

    private ImageInfo cachedImageInfo;
    private Date cachedImageInfoTime;

    public abstract InputStreamSource getInputStreamSource( );

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
        InputStreamSource source = getInputStreamSource();
        
        if (source instanceof FileInputStreamSource) {
            File file = ((FileInputStreamSource) source).getFile();
            
            return ImageOp.getImageInfo(Imcms.getServices().getConfig(), file);
        }
        
    	return ImageOp.getImageInfo(Imcms.getServices().getConfig(), source.getInputStream());
    }

    public String getCacheId() {
        String id = getUrlPathRelativeToContextPath();
        
        Date modified = getModifiedDatetime();
        
        id += "_" + (modified != null ? modified.getTime() : "0");
        
        InputStreamSource source = getInputStreamSource();
        try {
            id += "_" + source.getSize();
            
        } catch (IOException ex) {
            id += "_0";
        }
        
        return id;
    }
    
    public boolean isEmpty( ) {
        try {
            return getInputStreamSource().getSize() <= 0 ;
        } catch ( IOException e ) {
            return true ;
        }
    }

    public String getNameWithoutExt() {
        String name = getName();

        int periodIndex = name.lastIndexOf('.');
        if (periodIndex != -1) {
            name = name.substring(0, periodIndex);
        }

        return name;
    }

    public abstract String getName();
}

