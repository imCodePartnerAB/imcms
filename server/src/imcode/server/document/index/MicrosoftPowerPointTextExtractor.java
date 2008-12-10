package imcode.server.document.index;

import org.apache.poi.hslf.extractor.PowerPointExtractor;

import java.io.InputStream;
import java.io.IOException;

class MicrosoftPowerPointTextExtractor implements StreamTextsExtractor {

    public String[] extractTexts(InputStream in) throws IOException {
        PowerPointExtractor extractor = new PowerPointExtractor(in);
        return new String[] {extractor.getText(), extractor.getNotes()} ;
    }
}
