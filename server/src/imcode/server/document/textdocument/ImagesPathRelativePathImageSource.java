package imcode.server.document.textdocument;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.InputStreamSource;

import java.io.File;
import java.util.Date;

public class ImagesPathRelativePathImageSource extends ImageSource {
    private String path;

    public ImagesPathRelativePathImageSource( String path ) {
        this.path = path.replace('\\','/');
    }

    public InputStreamSource getInputStreamSource( ) {
        return new FileInputStreamSource( getFile( ) );
    }

    private File getFile( ) {
        ImcmsServices service = Imcms.getServices( );
        File basePath = isAbsolute() ? Imcms.getPath() : service.getConfig( ).getImagePath( ); 
        return new File( basePath, path );
    }

    public String getUrlPathRelativeToContextPath( ) {
        if (!isAbsolute()) {
            return getImagesUrlPath() + path ;
        }
        return path;
    }

    private boolean isAbsolute() {
        return path.startsWith("/");
    }

    public static String getImagesUrlPath() {
        return Imcms.getServices( ).getConfig( ).getImageUrl( );
    }

    public String toStorageString( ) {
        return path;
    }

    public int getTypeId( ) {
        return ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH;
    }

    public Date getModifiedDatetime( ) {
        return new Date( getFile( ).lastModified( ) );
    }

    public String getName() {
        return new File(path).getName();
    }
}
