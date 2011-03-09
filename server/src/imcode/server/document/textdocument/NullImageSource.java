package imcode.server.document.textdocument;

import imcode.util.io.InputStreamSource;
import imcode.util.io.EmptyInputStreamSource;

import java.util.Date;

public class NullImageSource extends ImageSource {
    public InputStreamSource getInputStreamSource( ) {
        return new EmptyInputStreamSource();
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

    public String getName() {
        return "";
    }
}
 
