package imcode.server.document.textdocument;

import imcode.util.InputStreamSource;

import java.util.Date;

public class NullImageSource extends ImageSource {
    public InputStreamSource getInputStreamSource( ) {
        return null;
    }

    public String getUrlPathRelativeToContextPath( ) {
        return "";
    }

    public String toStorageString( ) {
        return "";
    }

    public int getTypeId( ) {
        return ImageSource.IMAGE_TYPE_ID__NULL;
    }

    public Date getModifiedDatetime( ) {
        return null;
    }
}
 
