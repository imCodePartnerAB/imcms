package com.imcode.imcms.api;

import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.util.InputStreamSource;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.TransformedList;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileDocument extends Document {

    public final static int TYPE_ID = DocumentTypeDomainObject.FILE_ID ;

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

    public void addFile( String fileId, FileDocumentFile file) throws NoPermissionException {
        getSecurityChecker().hasEditPermission( this );
        getInternalFileDocument().addFile( fileId, file.getInternal() );
    }

    public static class FileDocumentFile implements DataSource {

        private FileDocumentDataSource dataSource ;

        public FileDocumentFile( DataSource dataSource ) {
            FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();
            file.setFilename( dataSource.getName() );
            file.setMimeType( dataSource.getContentType() );
            file.setInputStreamSource( new DataSourceInputStreamSource( dataSource ));
            this.dataSource = new FileDocumentDataSource( file ) ;
        }

        public FileDocumentFile( FileDocumentDomainObject.FileDocumentFile file ) {
            dataSource = new FileDocumentDataSource( file ) ;
        }

        public FileDocumentDomainObject.FileDocumentFile getInternal() {
            return dataSource.getFile() ;
        }

        public String getContentType() {
            return dataSource.getContentType();
        }

        public InputStream getInputStream() throws IOException {
            return dataSource.getInputStream();
        }

        public String getName() {
            return dataSource.getName();
        }

        /** @throws UnsupportedOperationException */
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException() ;
        }

        public long getSize() throws IOException {
            return dataSource.getFile().getInputStreamSource().getSize() ;
        }

        public String getId() {
            return dataSource.getFile().getId();
        }

    }

    private static class FileDocumentDataSource implements DataSource {

        private FileDocumentDomainObject.FileDocumentFile file;

        private FileDocumentDataSource( FileDocumentDomainObject.FileDocumentFile file ) {
            this.file = file;
        }

        public InputStream getInputStream() throws IOException {
            return file.getInputStreamSource().getInputStream();
        }

        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        public String getContentType() {
            return file.getMimeType();
        }

        public String getName() {
            return file.getFilename();
        }

        private FileDocumentDomainObject.FileDocumentFile getFile() {
            return file;
        }
    }

    private static class DataSourceInputStreamSource implements InputStreamSource {

        private final DataSource dataSource;

        private DataSourceInputStreamSource( DataSource dataSource ) {
            this.dataSource = dataSource;
        }

        public InputStream getInputStream() throws IOException {
            return dataSource.getInputStream();
        }

        public long getSize() throws IOException {
            return 0;
        }
    }
}
