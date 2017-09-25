package imcode.server.document;

import imcode.util.Utility;
import imcode.util.io.ExceptionFreeInputStreamSource;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.InputStreamSource;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.UnhandledException;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A FileDocumentDomainObject contains a collection files.
 * In this context a file is set of attributes associated with a data {@link FileDocumentFile}.
 * <p>
 * A file is identified by fileId - a string which is unique to a FileDocumentDomainObject.
 */
public class FileDocumentDomainObject extends DocumentDomainObject {

    public static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";

    @SuppressWarnings("unused")
    public static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;

    // key: file id
    private Map<String, FileDocumentFile> files = createFilesMap();

    private String defaultFileId;

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.FILE;
    }

    public void accept(DocumentVisitor documentVisitor) {
        documentVisitor.visitFileDocument(this);
    }

    public void addFile(String fileId, FileDocumentFile file) {
        if (null == fileId) {
            throw new NullArgumentException("fileId");
        }
        if (!files.containsKey(defaultFileId)) {
            defaultFileId = fileId;
        }
        FileDocumentFile fileClone = cloneFile(file);
        fileClone.setId(fileId);
        files.put(fileId, fileClone);
    }

    /**
     * @param file file to clone
     * @return file clone or null if provided file is null
     */
    private FileDocumentFile cloneFile(FileDocumentFile file) {
        if (null == file) {
            return null;
        }
        FileDocumentFile fileClone;
        try {
            fileClone = file.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
        return fileClone;
    }

    public Map<String, FileDocumentFile> getFiles() {
        Map<String, FileDocumentFile> map = createFilesMap();
        map.putAll(files);
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<String, FileDocumentFile> createFilesMap() {
        return MapUtils.orderedMap(new HashMap<String, FileDocumentFile>());
    }

    public FileDocumentFile getFile(String fileId) {
        return cloneFile(files.get(fileId));
    }

    public FileDocumentFile removeFile(String fileId) {
        FileDocumentFile fileDocumentFile = files.remove(fileId);
        selectDefaultFileName(fileId);
        return fileDocumentFile;
    }

    private void selectDefaultFileName(String fileId) {
        if (files.isEmpty()) {
            defaultFileId = null;
        } else if (defaultFileId.equals(fileId)) {
            defaultFileId = Utility.firstElementOfSetByOrderOf(files.keySet(), String.CASE_INSENSITIVE_ORDER);
        }
    }

    public String getDefaultFileId() {
        return defaultFileId;
    }

    public void setDefaultFileId(String defaultFileId) {
        if (!files.containsKey(defaultFileId)) {
            throw new IllegalArgumentException("Cannot set defaultFileId to non-existant key "
                    + defaultFileId);
        }
        this.defaultFileId = defaultFileId;
    }

    /**
     * @param fileId
     * @return file with fileId or default file if fileId is null or there is no file with a such id.
     */
    public FileDocumentFile getFileOrDefault(String fileId) {
        if (null == fileId) {
            return getDefaultFile();
        }
        FileDocumentFile fileDocumentFile = getFile(fileId);
        if (null == fileDocumentFile) {
            fileDocumentFile = getDefaultFile();
        }
        return fileDocumentFile;
    }

    public FileDocumentFile getDefaultFile() {
        return getFile(defaultFileId);

    }

    @SuppressWarnings("unused")
    public void changeFileId(String oldFileId, String newFileId) {
        if (null == oldFileId) {
            throw new NullArgumentException("oldFileId");
        }
        if (null == newFileId) {
            throw new NullArgumentException("newFileId");
        }
        if (!files.containsKey(oldFileId)) {
            throw new IllegalStateException("There is no file with the id " + oldFileId);
        }
        if (oldFileId.equals(newFileId)) {
            return;
        }
        if (files.containsKey(newFileId)) {
            throw new IllegalStateException("There already is a file with the id " + newFileId);
        }
        addFile(newFileId, files.remove(oldFileId));
        if (defaultFileId.equals(oldFileId)) {
            defaultFileId = newFileId;
        }
    }

    /**
     * File attributes associated with a data.
     *
     * @see imcode.util.io.InputStreamSource
     */
    public static class FileDocumentFile implements Cloneable, Serializable {

        private String id;

        /**
         * If this object represent a new file then assigned by the system before the file is stored in a FS.
         * Otherwise set by the system when FileDocumentDomainObject is initialized.
         */
        private String filename;

        private String mimeType;
        private InputStreamSource inputStreamSource;
        private boolean createdAsImage;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String v) {
            this.filename = v;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public InputStreamSource getInputStreamSource() {
            return new ExceptionFreeInputStreamSource(inputStreamSource);
        }

        public boolean isFileInputStreamSource() {
            return inputStreamSource instanceof FileInputStreamSource;
        }

        public void setInputStreamSource(InputStreamSource inputStreamSource) {
            this.inputStreamSource = inputStreamSource;
        }

        public boolean isCreatedAsImage() {
            return createdAsImage;
        }

        public void setCreatedAsImage(boolean createdAsImage) {
            this.createdAsImage = createdAsImage;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public FileDocumentFile clone() throws CloneNotSupportedException {
            return (FileDocumentFile) super.clone();
        }

        public File getFile() {
            return ((FileInputStreamSource) inputStreamSource).getFile();
        }
    }
}