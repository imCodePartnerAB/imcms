package com.imcode.imcms.api;

import com.imcode.imcms.api.util.InputStreamSource;
import imcode.server.document.FileDocumentDomainObject;
import imcode.util.FileInputStreamSource;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.collections.map.TransformedMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileDocument extends Document {

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

    public Map getFiles() throws NoPermissionException {
        getSecurityChecker().hasAtLeastDocumentReadPermission( this );
        Map fileMap = TransformedMap.decorate(new HashMap(), TransformerUtils.nopTransformer(), new Transformer() {
            public Object transform( Object input ) {
                return new FileDocumentFile( (FileDocumentDomainObject.FileDocumentFile)input ) ;
            }
        }) ;
        fileMap.putAll( getInternalFileDocument().getFiles() );
        return fileMap;
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
