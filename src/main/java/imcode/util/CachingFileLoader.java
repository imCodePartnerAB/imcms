package imcode.util;

import imcode.server.Imcms;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

@Component
public class CachingFileLoader {

    private static final int FILE_CACHE_SIZE = 100;

    private Map<File, Map.Entry<String, Long>> fileCache = createSynchronizedMapWithInitialSize(FILE_CACHE_SIZE);
    private Map<Path, String> pathToContentCache = createSynchronizedMapWithInitialSize(FILE_CACHE_SIZE);

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> createSynchronizedMapWithInitialSize(int size) {
        return Collections.synchronizedMap((Map<K, V>) new LRUMap(size));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> createSynchronizedMapDefaultInitialSize() {
        return createSynchronizedMapWithInitialSize(FILE_CACHE_SIZE);
    }

    /**
     * Fetch a file from the cache, if it hasn't changed on disk.
     */
    public String getCachedFileString(File file) throws IOException {
        String cachedContents = getCachedFileStringIfRecent(file);
        if (null == cachedContents) {
            final byte[] fileBytes = readFile(file);
            cachedContents = decodeBytes(fileBytes, file.toString());
            cacheFile(file, cachedContents);
        }
        return cachedContents;
    }

    /**
     * Fetch a file content from the cache.
     */
    public String getCachedFileString(Path filePath) throws IOException {
        String content = pathToContentCache.get(filePath);

        if (content == null) {
            final byte[] fileBytes = readPath(filePath);
            content = decodeBytes(fileBytes, filePath.toString());
            pathToContentCache.put(filePath, content);
        }

        return content;
    }

    private String decodeBytes(byte[] inputBytes, String resourceName) {
        Charset utf8Charset = Charset.forName(Imcms.UTF_8_ENCODING);
        Charset fallbackCharset = Charset.defaultCharset();
        if (fallbackCharset.equals(utf8Charset) || fallbackCharset.equals(Charset.forName(Imcms.ASCII_ENCODING))) {
            fallbackCharset = Charset.forName(Imcms.ISO_8859_1_ENCODING);
        }

        return new FallbackDecoder(utf8Charset, fallbackCharset).decodeBytes(inputBytes, resourceName);
    }

    private byte[] readPath(Path readMe) throws IOException {
        return readFromStream(Files.newInputStream(readMe));
    }

    private byte[] readFile(File readMe) throws IOException {
        return readFromStream(new FileInputStream(readMe));
    }

    public String getCachedFileStringIfRecent(File file) {
        Map.Entry<String, Long> fileAndDate = fileCache.get(file);
        if (fileAndDate != null && file.lastModified() <= fileAndDate.getValue()) {
            return fileAndDate.getKey();
        }
        return null;
    }

    private byte[] readFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, os);
        return os.toByteArray();
    }

    private void cacheFile(File file, String contents) {
        Map<String, Long> contentTime = createSynchronizedMapWithInitialSize(1);
        contentTime.put(contents, System.currentTimeMillis());
        fileCache.put(file, contentTime.entrySet().iterator().next());
    }
}
