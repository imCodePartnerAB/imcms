package imcode.server.document.index;

import com.imcode.imcms.servlet.tags.MenuTag;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

class IndexDocumentAdaptingVisitor extends DocumentVisitor {

//    private static final String MIME_TYPE__WORD = "application/msword";
//    private static final String MIME_TYPE__EXCEL = "application/vnd.ms-excel";
//    private static final String MIME_TYPE__POWERPOINT = "application/vnd.ms-powerpoint";
//    private static final String MIME_TYPE__PDF = "application/pdf";

    Document indexDocument;
    Tika tikaAutodetect;
    Tika tikaHtml;

    private final static Logger log = Logger.getLogger(IndexDocumentFactory.class.getName());

    IndexDocumentAdaptingVisitor(Document indexDocument, Tika tikaAutodetect, Tika tikaHtml) {
        this.indexDocument = indexDocument;
        this.tikaAutodetect = tikaAutodetect;
        this.tikaHtml = tikaHtml;
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

        for ( MenuDomainObject menu : textDocument.getMenus().values() ) {
            for ( MenuItemDomainObject menuItem : menu.getMenuItems() ) {
                indexDocument.add(Field.Keyword(DocumentIndex.FIELD__CHILD_ID, ""+menuItem.getDocumentId()));
            }
        }

        for ( ImageDomainObject image : textDocument.getImages().values() ) {
            String imageLinkUrl = image.getLinkUrl();
            if ( null != imageLinkUrl && imageLinkUrl.length() > 0 ) {
                indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl));
            }
        }
    }

//    private String stripHtml(TextDomainObject text) {
//        String string = text.getText();
//        if ( TextDomainObject.TEXT_TYPE_HTML == text.getType() ) {
//            string = string.replaceAll("<[^>]+?>", "");
//        }
//        return string;
//    }

    private String stripHtml(TextDomainObject text) {
        String string = text.getText();

        if (text.getType() != TextDomainObject.TEXT_TYPE_HTML) {
            try {
                String stripped = tikaHtml.parseToString(IOUtils.toInputStream(string));
                log.trace(String.format("Stripped html to plain text: '%s' -> '%s'", string, stripped));
                string = stripped;
            } catch (Exception e) {
                log.error(String.format("Unable to strip html '%s'", string), e);
            }
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

//    private final static Map EXTRACTORS = new HashMap(ArrayUtils.toMap(new Object[][] {
//            { MIME_TYPE__WORD, new MicrosoftWordTextExtractor() },
//            { MIME_TYPE__EXCEL, new MicrosoftExcelTextExtractor() },
//            { MIME_TYPE__POWERPOINT, new MicrosoftPowerPointTextExtractor() },
//            { MIME_TYPE__PDF, new PdfTextExtractor() },
//    }));

//    private void indexFileContents(FileDocumentDomainObject.FileDocumentFile file) {
//        String mimeType = file.getMimeType();
//        StreamTextsExtractor extractor = (StreamTextsExtractor) EXTRACTORS.get(mimeType);
//        if ( null == extractor ) {
//            return;
//        }
//        String[] texts;
//        InputStream in = null;
//        try {
//            in = file.getInputStreamSource().getInputStream();
//
//            texts = extractor.extractTexts(in);
//        } catch ( IOException ioe ) {
//            throw new RuntimeException(ioe);
//        } finally {
//            IOUtils.closeQuietly(in);
//        }
//
//        for ( String text : texts ) {
//            indexDocument.add(Field.UnStored(DocumentIndex.FIELD__TEXT, text));
//        }
//    }

    private void indexFileContents(FileDocumentDomainObject.FileDocumentFile file) {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_DISPOSITION, file.getFilename());
        metadata.set(Metadata.CONTENT_TYPE, file.getMimeType());

        try {
            String content = tikaAutodetect.parseToString(file.getInputStreamSource().getInputStream());
            indexDocument.add(Field.UnStored(DocumentIndex.FIELD__TEXT, content));
        } catch (Exception e) {
            log.error(String.format("Unable to index content of file-doc-file '%s'", file), e);
        }
    }
}