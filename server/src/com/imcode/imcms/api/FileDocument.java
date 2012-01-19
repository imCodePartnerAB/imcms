package com.imcode.imcms.api;

import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.util.io.InputStreamSource;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.TransformedList;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Document holding a file or files.
 */
public class FileDocument extends Document {

    /**
     * FileDocument TYPE_ID
     */
    public final static int TYPE_ID = DocumentTypeDomainObject.FILE_ID ;

    FileDocument( FileDocumentDomainObject document, ContentManagementSystem contentManagementSystem ) {
        super( document, contentManagementSystem );
    }

    private FileDocumentDomainObject getInternalFileDocument() {
        return (FileDocumentDomainObject)getInternal() ;
    }

    /**
     * Returns a {@link FileDocumentFile} with the given id
     * @param fileId id of a {@link FileDocument} file.
     * @return FileDocumentFile with the given id
     */
    public FileDocumentFile getFile( String fileId ) {
        return new FileDocumentFile(getInternalFileDocument().getFile( fileId ));
    }

    /**
     * Removes a {@link FileDocumentFile} with the given id.
     * If the returned filedocument file was the default one, sets the next filedocument file in case insensitive order
     * as the default one or null if no filedocument files left.
     * @param fileId {@link FileDocumentFile} id to remove.
     * @return the removed {@link FileDocumentFile}
     */
    public FileDocumentFile removeFile( String fileId ) {
        return new FileDocumentFile( getInternalFileDocument().removeFile( fileId ) );
    }

    /**
     * Returns all filedocument files in this file filedocument.
     * @return not null, an array of {@link FileDocumentFile}
     */
    public FileDocumentFile[] getFiles() {
        Map filesMap = getInternalFileDocument().getFiles();
        List files = TransformedList.decorate(new ArrayList(filesMap.size()), new Transformer() {
            public Object transform( Object input ) {
                return new FileDocumentFile( (FileDocumentDomainObject.FileDocumentFile)input ) ;
            }
        }) ;
        files.addAll( filesMap.values() ) ;
        return (FileDocumentFile[])files.toArray( new FileDocumentFile[files.size()] );
    }

    /**
     * Returns FileDocumentFile with given id. The default FileDocumentFile is returned if the id is null or there is
     * no FileDocumentFile with such id.
     * @param fileId id of FileDocumentFile
     * @return FileDocumentFile with given id or null if fileId is null or none found with given id.
     */
    public FileDocumentFile getFileOrDefault( String fileId ) {
        return new FileDocumentFile( getInternalFileDocument().getFileOrDefault( fileId ) );
    }

    /**
     * Returns the default file id
     * @return default file id or null if there's no default file
     */
    public String getDefaultFileId() {
        return getInternalFileDocument().getDefaultFileId();
    }

    /**
     * Returns the default {@link FileDocumentFile} of this {@link FileDocument}
     * @return the default {@link FileDocumentFile} or null if there's no default file
     */
    public FileDocumentFile getDefaultFile() {
        return new FileDocumentFile( getInternalFileDocument().getDefaultFile() );
    }

    /**
     * Adds a file to the file document
     * The added file becomes the default one if there's no default file.
     * @param fileId id of the file to be added, not null
     * @param file file to add
     */
    public void addFile( String fileId, FileDocumentFile file) {
        getInternalFileDocument().addFile( fileId, file.getInternal() );
    }

    /**
     * Class representing a file contained in a {@link FileDocument}
     */
    public static class FileDocumentFile implements DataSource {

        private FileDocumentDataSource dataSource ;

        /**
         * Constructs FileDocumentFile from the given {@link javax.activation.DataSource}
         * @param dataSource data source of a file for this FileDocumentFile
         */
        public FileDocumentFile( DataSource dataSource ) {
            FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();
            file.setFilename( dataSource.getName() );
            file.setMimeType( dataSource.getContentType() );
            file.setInputStreamSource( new DataSourceInputStreamSource( dataSource ));
            this.dataSource = new FileDocumentDataSource( file ) ;
        }

        /**
         * Constructs FileDocumentFile from another FileDocumentFile
         * @param file FileDocumentFile
         */
        public FileDocumentFile( FileDocumentDomainObject.FileDocumentFile file ) {
            dataSource = new FileDocumentDataSource( file ) ;
        }

        /**
         * Returns internally used FileDocumentFile
         * @return internally used FileDocumentFile
         */
        public FileDocumentDomainObject.FileDocumentFile getInternal() {
            return dataSource.getFile() ;
        }

        /**
         * Returns the content type of this FileDocumentFile
         * @return a String representing content type
         */
        public String getContentType() {
            return dataSource.getContentType();
        }

        /**
         * Returns {@link java.io.InputStream} of this FileDocumentFile
         * @return {@link java.io.InputStream} of this FileDocumentFile
         * @throws IOException
         */
        public InputStream getInputStream() throws IOException {
            return dataSource.getInputStream();
        }

        /**
         * Returns the name of this FileDocumentFile's data source
         * @return a String with the name of this FileDocumentFile's data source
         */
        public String getName() {
            return dataSource.getName();
        }

        /**
         * Not supported.
         * @throws UnsupportedOperationException to signal that this is not supported
         */
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException() ;
        }

        /**
         * Returns the size of underlying data source's file input stream
         * @return input stream's size in long
         * @throws IOException
         */
        public long getSize() throws IOException {
            return dataSource.getFile().getInputStreamSource().getSize() ;
        }

        /**
         * Returns the id of underlying data source file
         * @return a String representing underlying data source file's id
         */
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
