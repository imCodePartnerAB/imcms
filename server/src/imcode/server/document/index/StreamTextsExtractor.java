package imcode.server.document.index;

import java.io.IOException;
import java.io.InputStream;

interface StreamTextsExtractor {
    String[] extractTexts(InputStream in) throws IOException;
}
