package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import imcode.server.Imcms;
import imcode.util.io.InputStreamSource;
import imcode.util.io.StorageInputStreamSource;

import java.util.Date;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileStorageImageSource extends ImageSource{

    private final StoragePath path;
    private InputStreamSource inputStreamSource;

    private final static StoragePath imageStoragePath = StoragePath.get(DIRECTORY,  Imcms.getServices().getConfig().getImageUrl());

    @JsonCreator
    public FileStorageImageSource(@JsonProperty("urlPathRelativeToContextPath") String path) {
        this.path = imageStoragePath.resolve(FILE, path);
    }

    @Override
    public InputStreamSource getInputStreamSource() {
        if(inputStreamSource == null){
            inputStreamSource = new StorageInputStreamSource(path, getStorageClient());
        }

        return inputStreamSource;
    }

    @Override
    public String getUrlPathRelativeToContextPath() {
        return Imcms.getServices()
                .getManagedBean("storageImagePath", String.class) + StoragePath.PATH_SEPARATOR + path.toString();
    }

    @Override
    public String toStorageString() {
        return imageStoragePath.relativize(path).toString();
    }

    @Override
    public int getTypeId() {
        return ImageSource.IMAGE_TYPE_ID__FILE_STORAGE;
    }

    @Override
    public Date getModifiedDatetime() {
        long time = getStorageClient().getFile(path).lastModified();
        return new Date(time);
    }

    @Override
    public String getName() {
        return path.getName();
    }

    @Override
    public boolean isEmpty() {
        return !getStorageClient().exists(path);
    }

    private StorageClient getStorageClient(){
        return Imcms.getServices()
                .getManagedBean("imageStorageClient", StorageClient.class);
    }

}
