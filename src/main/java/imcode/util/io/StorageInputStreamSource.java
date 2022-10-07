package imcode.util.io;

import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;

import java.io.IOException;
import java.io.InputStream;

public class StorageInputStreamSource implements InputStreamSource{

    private final StoragePath path;
    private final StorageClient storageClient;

    public StorageInputStreamSource(StoragePath path, StorageClient storageClient){
        this.path = path;
        this.storageClient = storageClient;
    }

    @Override
    public long getSize() throws IOException {
        try (StorageFile file = storageClient.getFile(path)){
            return file.size();
        } catch(StorageFileNotFoundException e){
            return 0;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            final StorageFile file = storageClient.getFile(path);
            final InputStream content = file.getContent();

            return new InputStream() {
                @Override
                public int read() throws IOException {
                    return content.read();
                }

                @Override
                public void close() throws IOException {
                        file.close();
                }
            };
        } catch (StorageFileNotFoundException e){
            return new EmptyInputStream();
        }
    }
}
