package imcode.server.document.textdocument;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;

import java.io.File;
import java.util.Date;

public class ImagesPathRelativePathImageSource extends ImageSource {
    private String relativePath;

    public ImagesPathRelativePathImageSource( String relativePath ) {
        this.relativePath = relativePath.replace('\\','/');
    }

    public InputStreamSource getInputStreamSource( ) {
        return new FileInputStreamSource( getFile( ) );
    }

    private File getFile( ) {
        ImcmsServices service = Imcms.getServices( );
        File imageDirectory = service.getConfig( ).getImagePath( );
        return new File( imageDirectory, relativePath );
    }

    public String getUrlPathRelativeToContextPath( ) {
        File file = new File ( relativePath );
        return getImagePath() + FileUtility.relativeFileToString( file );
    }

    public static String getImagePath() {
        return Imcms.getServices( ).getConfig( ).getImageUrl( );
    }

    public String toStorageString( ) {
        return relativePath;
    }

    public int getTypeId( ) {
        return ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH;
    }

    public Date getModifiedDatetime( ) {
        return new Date( getFile( ).lastModified( ) );
    }
}
