package imcode.server.document.index;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.SAXException;

import java.util.*;

/**
 * referenced from <a href="https://svn.apache.org/repos/asf/tika/trunk/tika-example/src/main/java/org/apache/tika/example/ContentHandlerExample.java">ContentHandlerExample.class</a>
 */
public class ChunkHandler extends ContentHandlerDecorator {
	private static final int MAXIMUM_TEXT_CHUNK_SIZE = 100 * 1024 * 1024;
	private final List<String> chunks = new LinkedList<>(Collections.singletonList(" "));


	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		final String lastChunk = chunks.get(chunks.size() - 1);
		final String thisStr = new String(ch, start, length);

		if (lastChunk.length() + length > MAXIMUM_TEXT_CHUNK_SIZE) {
			chunks.add(thisStr);
		} else {
			chunks.set(chunks.size() - 1, lastChunk + thisStr);
		}
	}

	public List<String> getChunks() {
		return chunks;
	}

	public String toString() {
		return StringUtils.join(getChunks(), ' ');
	}
}
