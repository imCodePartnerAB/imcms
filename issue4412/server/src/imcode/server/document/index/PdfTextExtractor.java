package imcode.server.document.index;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;

class PdfTextExtractor implements StreamTextsExtractor {

    public String[] extractTexts(InputStream in) throws IOException {
        PDDocument pdf = PDDocument.load(in);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(pdf) ;
        return new String[] {text} ;
    }
}
