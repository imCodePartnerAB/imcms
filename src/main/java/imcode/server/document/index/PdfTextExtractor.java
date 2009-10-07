package imcode.server.document.index;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

class PdfTextExtractor implements StreamTextsExtractor {

    public String[] extractTexts(InputStream in) throws IOException {
        PDDocument pdf = PDDocument.load(in);
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf) ;
            return new String[] {text} ;
        } finally {
            pdf.close();
        }
    }
}
