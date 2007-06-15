package imcode.server.document.index;

import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class IndexDocumentAdaptingVisitor extends DocumentVisitor {

    private static final String MIME_TYPE__WORD = "application/msword";
    private static final String MIME_TYPE__EXCEL = "application/vnd.ms-excel";
    private static final String MIME_TYPE__POWERPOINT = "application/vnd.ms-powerpoint";
    private static final String MIME_TYPE__PDF = "application/pdf";

    Document indexDocument;

    IndexDocumentAdaptingVisitor(Document indexDocument) {
        this.indexDocument = indexDocument;
    }

    public void visitTextDocument(TextDocumentDomainObject textDocument) {
        indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__TEMPLATE, textDocument.getTemplateName()));

        for ( Map.Entry<Integer,TextDomainObject> textEntry : textDocument.getTexts().entrySet() ) {
            Integer textIndex = textEntry.getKey();
            TextDomainObject text = textEntry.getValue();
            indexDocument.add(Field.UnStored(DocumentIndex.FIELD__NONSTRIPPED_TEXT, text.getText()));
            String htmlStrippedText = stripHtml(text);
            indexDocument.add(Field.UnStored(DocumentIndex.FIELD__TEXT, htmlStrippedText));
            indexDocument.add(Field.UnStored(DocumentIndex.FIELD__TEXT + textIndex, htmlStrippedText));
        }

        for ( ImageDomainObject image : textDocument.getImages().values() ) {
            String imageLinkUrl = image.getLinkUrl();
            if ( null != imageLinkUrl && imageLinkUrl.length() > 0 ) {
                indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl));
            }
        }
    }

    private String stripHtml(TextDomainObject text) {
        String string = text.getText();
        if ( TextDomainObject.TEXT_TYPE_HTML == text.getType() ) {
            string = string.replaceAll("<[^>]+?>", "");
        }
        return string;
    }

    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        FileDocumentDomainObject.FileDocumentFile file = fileDocument.getDefaultFile();
        if ( null == file ) {
            return;
        }
        indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType()));
        indexFileContents(file);
    }

    private final static Map EXTRACTORS = new HashMap(ArrayUtils.toMap(new Object[][] {
            { MIME_TYPE__WORD, new MicrosoftWordTextExtractor() },
            { MIME_TYPE__EXCEL, new MicrosoftExcelTextExtractor() },
            { MIME_TYPE__POWERPOINT, new MicrosoftPowerPointTextExtractor() },
            { MIME_TYPE__PDF, new PdfTextExtractor() },
    }));

    private void indexFileContents(FileDocumentDomainObject.FileDocumentFile file) {
        String mimeType = file.getMimeType();
        StreamTextsExtractor extractor = (StreamTextsExtractor) EXTRACTORS.get(mimeType);
        if ( null == extractor ) {
            return;
        }
        String[] texts;
        try {
            texts = extractor.extractTexts(file.getInputStreamSource().getInputStream());
        } catch ( IOException ioe ) {
            throw new RuntimeException(ioe);
        }
        for ( String text : texts ) {
            indexDocument.add(Field.UnStored(DocumentIndex.FIELD__TEXT, text));
        }
    }

}
