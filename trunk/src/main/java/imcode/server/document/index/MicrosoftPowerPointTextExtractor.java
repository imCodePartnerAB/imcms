package imcode.server.document.index;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hslf.extractor.PowerPointExtractor;

class MicrosoftPowerPointTextExtractor implements StreamTextsExtractor {

    public String[] extractTexts(InputStream in) throws IOException {
        PowerPointExtractor extractor = new PowerPointExtractor(in);
        return new String[] {extractor.getText(), extractor.getNotes()} ;
    }
}
