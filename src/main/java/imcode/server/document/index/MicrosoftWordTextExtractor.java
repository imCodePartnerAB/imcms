package imcode.server.document.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.TextPieceTable;

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
