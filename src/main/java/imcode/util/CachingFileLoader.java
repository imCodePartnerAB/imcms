package imcode.util;

import imcode.server.Imcms;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

public class CachingFileLoader {

	private static final int FILE_CACHE_SIZE = 100;

	private Map<File, Map.Entry<String, Long>> fileCache = createSynchroMapWithInitialSize(FILE_CACHE_SIZE);

	/**
	 * Fetch a file from the cache, if it hasn't changed on disk.
	 */
	public String getCachedFileString(File file) throws IOException {
		String cachedContents = getCachedFileStringIfRecent(file);
		if (null == cachedContents) {
			cachedContents = decodeFile(file);
			cacheFile(file, cachedContents);
		}
		return cachedContents;
	}

	private String decodeFile(File file) throws IOException {
		Charset utf8Charset = Charset.forName(Imcms.UTF_8_ENCODING);
		Charset fallbackCharset = Charset.defaultCharset();
		if (fallbackCharset.equals(utf8Charset) || fallbackCharset.equals(Charset.forName(Imcms.ASCII_ENCODING))) {
			fallbackCharset = Charset.forName(Imcms.ISO_8859_1_ENCODING);
		}
		return decodeFile(file, utf8Charset, fallbackCharset);
	}

	private String decodeFile(File file, Charset charset, Charset fallbackCharset) throws IOException {
		return new FallbackDecoder(charset, fallbackCharset).decodeBytes(readFile(file), "file " + file.toString());
	}

	private byte[] readFile(File file) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(file);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtils.copy(fileInputStream, os);
		return os.toByteArray();
	}

	private void cacheFile(File file, String contents) {
		Map<String, Long> contentTime = createSynchroMapWithInitialSize(1);
		contentTime.put(contents, System.currentTimeMillis());
		fileCache.put(file, contentTime.entrySet().iterator().next());
	}

	public String getCachedFileStringIfRecent(File file) {
		Map.Entry<String, Long> fileAndDate = fileCache.get(file);
		if (file.lastModified() <= fileAndDate.getValue()) {
			return fileAndDate.getKey();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <K, V> Map<K, V> createSynchroMapWithInitialSize(int size) {
		return Collections.synchronizedMap((Map<K, V>) new LRUMap(size));
	}
}
