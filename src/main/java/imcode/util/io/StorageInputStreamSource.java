package imcode.util.io;

import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StorageInputStreamSource implements InputStreamSource{

    private final StoragePath path;
    private final StorageClient storageClient;

    private byte[] content;

    public StorageInputStreamSource(StoragePath path, StorageClient storageClient){
        this.path = path;
        this.storageClient = storageClient;
    }

    @Override
    public long getSize() throws IOException {
        if(content == null){
            try(InputStream inputStream = getInputStream()){
                return inputStream.available();
            }
        }

        return content.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if(content == null){
            try (StorageFile file = storageClient.getFile(path)){
                content = IOUtils.toByteArray(file.getContent());
            }catch (StorageFileNotFoundException e){
                content = new byte[0];
            }
        }

        return new ByteArrayInputStream(content);
    }
}
