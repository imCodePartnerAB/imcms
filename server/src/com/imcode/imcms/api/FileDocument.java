package com.imcode.imcms.api;

import com.imcode.imcms.api.util.InputStreamSource;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.util.FileInputStreamSource;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.TransformedList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileDocument extends Document {

    public final static int TYPE_ID = DocumentDomainObject.DOCTYPE_FILE ;

    FileDocument( FileDocumentDomainObject document, ContentManagementSystem contentManagementSystem ) {
        super( document, contentManagementSystem );
    }

    private FileDocumentDomainObject getInternalFileDocument() {
        return (FileDocumentDomainObject)getInternal() ;
    }

    public FileDocumentFile getFile( String fileId ) throws NoPermissionException {
        getSecurityChecker().hasAtLeastDocumentReadPermission( this );
        return new FileDocumentFile(getInternalFileDocument().getFile( fileId ));
    }

    public FileDocumentFile removeFile( String fileId ) throws NoPermissionException {
        getSecurityChecker().hasEditPermission( this );
        return new FileDocumentFile( getInternalFileDocument().removeFile( fileId ) );
    }

    public FileDocumentFile[] getFiles() throws NoPermissionException {
        getSecurityChecker().hasAtLeastDocumentReadPermission( this );
        Map filesMap = getInternalFileDocument().getFiles();
        List files = TransformedList.decorate(new ArrayList(filesMap.size()), new Transformer() {
            public Object transform( Object input ) {
                return new FileDocumentFile( (FileDocumentDomainObject.FileDocumentFile)input ) ;
            }
        }) ;
        files.addAll( filesMap.values() ) ;
        return (FileDocumentFile[])files.toArray( new FileDocumentFile[files.size()] );
    }

    public FileDocumentFile getFileOrDefault( String fileId ) throws NoPermissionException {
        getSecurityChecker().hasAtLeastDocumentReadPermission( this );
        return new FileDocumentFile( getInternalFileDocument().getFileOrDefault( fileId ) );
    }

    public String getDefaultFileId() throws NoPermissionException {
        getSecurityChecker().hasAtLeastDocumentReadPermission( this );
        return getInternalFileDocument().getDefaultFileId();
    }

    public FileDocumentFile getDefaultFile() throws NoPermissionException {
        getSecurityChecker().hasAtLeastDocumentReadPermission( this );
        return new FileDocumentFile( getInternalFileDocument().getDefaultFile() );
    }

    public void addFile( String fileId, FileDocumentFile file ) throws NoPermissionException {
        getSecurityChecker().hasEditPermission( this );
        getInternalFileDocument().addFile( fileId, file.internalFile );
    }

    public static class FileDocumentFile {

        private FileDocumentDomainObject.FileDocumentFile internalFile;

        public FileDocumentFile( FileDocumentDomainObject.FileDocumentFile file ) {
            internalFile = file;
        }

        public FileDocumentFile() {
            this(new FileDocumentDomainObject.FileDocumentFile());
        }

        public FileDocumentFile( String filename, String mimeType, InputStreamSource inputStreamSource ) {
            this() ;
            setFilename( filename );
            setMimeType( mimeType );
            setInputStreamSource( inputStreamSource );
        }

        public FileDocumentFile( File file, String mimeType ) {
            this( file.getName(), mimeType, new FileInputStreamSource( file ) );
        }

        public String getMimeType() {
            return internalFile.getMimeType();
        }

        public String getFilename() {
            return internalFile.getFilename();
        }

        public InputStreamSource getInputStreamSource() {
            return internalFile.getInputStreamSource();
        }

        public String getId() {
            return internalFile.getId() ;
        }

        public void setInputStreamSource( InputStreamSource inputStreamSource ) {
            internalFile.setInputStreamSource( inputStreamSource );
        }

        public void setFilename( String v ) {
            internalFile.setFilename( v );
        }

        public void setMimeType( String mimeType ) {
            internalFile.setMimeType( mimeType );
        }
    }

}
