package imcode.server.document;

import com.imcode.imcms.api.util.InputStreamSource;
import imcode.util.Utility;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.NullArgumentException;

import java.util.HashMap;
import java.util.Map;

public class FileDocumentDomainObject extends DocumentDomainObject {

    private Map files = createFilesMap();

    private String defaultFileId ;
    public static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        // nothing lazily loaded
    }

    public int getDocumentTypeId() {
        return DOCTYPE_FILE;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitFileDocument( this );
    }

    public void addFile( String fileId, FileDocumentFile fileDocumentFile ) {
        if ( null == fileId ) {
            throw new NullArgumentException( "fileId" );
        }
        if ( !files.containsKey( defaultFileId ) ) {
            defaultFileId = fileId;
        }
        files.put( fileId, fileDocumentFile );
    }

    public Map getFiles() {
        Map map = createFilesMap();
        map.putAll( files ) ;
        return map;
    }

    private Map createFilesMap() {
        return MapUtils.orderedMap( new HashMap() );
    }

    public FileDocumentFile getFile( String fileId ) {
        return (FileDocumentFile)files.get( fileId );
    }

    public FileDocumentFile removeFile( String fileId ) {
        FileDocumentFile fileDocumentFile = (FileDocumentFile)files.remove( fileId );
        selectDefaultFileName( fileId );
        return fileDocumentFile;
    }

    private void selectDefaultFileName( String fileId ) {
        if (files.isEmpty()) {
            defaultFileId = null ;
        } else if ( defaultFileId.equals( fileId ) ) {
            defaultFileId = (String)Utility.firstElementOfSetByOrderOf( files.keySet(), String.CASE_INSENSITIVE_ORDER ) ;
        }
    }

    public void setDefaultFileId( String defaultFileId ) {
        if ( !files.containsKey( defaultFileId ) ) {
            throw new IllegalArgumentException( "Cannot set defaultFileId to non-existant key "
                                                + defaultFileId );
        }
        this.defaultFileId = defaultFileId;
    }

    public String getDefaultFileId() {
        return defaultFileId;
    }

    public FileDocumentFile getFileOrDefault( String fileId ) {
        FileDocumentFile fileDocumentFile = getFile( fileId );
        if ( null == fileDocumentFile ) {
            fileDocumentFile = getDefaultFile();
        }
        return fileDocumentFile;
    }

    public FileDocumentFile getDefaultFile() {
        return (FileDocumentFile)files.get( defaultFileId );

    }

    public void changeFileId( String oldFileId, String newFileId ) {
        if ( null == oldFileId ) {
            throw new NullArgumentException( "oldFileId" );
        }
        if ( null == newFileId ) {
            throw new NullArgumentException( "newFileId" );
        }
        if (!files.containsKey( oldFileId )) {
            throw new IllegalStateException( "There is no file with the id " + oldFileId );
        }
        if (oldFileId.equals( newFileId )) {
            return ;
        }
        if (files.containsKey( newFileId )) {
            throw new IllegalStateException( "There already is a file with the id "+newFileId ) ;
        }
        files.put(newFileId, files.remove( oldFileId )) ;
        if (defaultFileId.equals( oldFileId )) {
            defaultFileId = newFileId ;
        }
    }

    public static class FileDocumentFile {

        private String filename;
        private String mimeType = MIME_TYPE__UNKNOWN_DEFAULT;
        private InputStreamSource inputStreamSource;
        private boolean createdAsImage;

        public String getFilename() {
            return filename;
        }

        public void setFilename( String v ) {
            this.filename = v;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType( String mimeType ) {
            this.mimeType = mimeType;
        }

        public void setInputStreamSource( InputStreamSource inputStreamSource ) {
            this.inputStreamSource = inputStreamSource;
        }

        public InputStreamSource getInputStreamSource() {
            return inputStreamSource;
        }

        public void setCreatedAsImage( boolean createdAsImage ) {
            this.createdAsImage = createdAsImage;
        }

        public boolean isCreatedAsImage() {
            return createdAsImage;
        }
    }
}