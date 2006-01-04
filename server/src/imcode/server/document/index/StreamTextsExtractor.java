package imcode.server.document.index;

import java.io.InputStream;
import java.io.IOException;

interface StreamTextsExtractor {
    String[] extractTexts(InputStream in) throws IOException;
}
