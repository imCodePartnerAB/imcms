package imcode.server.document.index;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.model.TextPiece;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

class MicrosoftWordTextExtractor implements StreamTextsExtractor {

    public String[] extractTexts(InputStream in) throws IOException {
        List texts = new ArrayList();
        HWPFDocument wordDocument = new HWPFDocument(in);
        TextPieceTable textTable = wordDocument.getTextTable();
        List textPieces = textTable.getTextPieces();
        for ( Iterator iterator = textPieces.iterator(); iterator.hasNext(); ) {
            TextPiece textPiece = (TextPiece) iterator.next();
            String text = textPiece.getStringBuffer().toString();
            text = text.replaceAll("\\x13.*\\x14", "");
            text = text.replaceAll("[\\x00-\\x1F]+", " ");
            texts.add(text);
        }
        return (String[]) texts.toArray(new String[texts.size()]);
    }
}
