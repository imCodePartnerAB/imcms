package imcode.server.document.index;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
//import org.pdfbox.pdmodel.PDDocument;
//import org.pdfbox.util.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

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
