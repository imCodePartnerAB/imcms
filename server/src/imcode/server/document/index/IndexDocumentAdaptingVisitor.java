package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.*;
import org.apache.lucene.util.BytesRef;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

import java.io.InputStream;
import java.util.Map;

class IndexDocumentAdaptingVisitor extends DocumentVisitor {

//    private static final String MIME_TYPE__WORD = "application/msword";
//    private static final String MIME_TYPE__EXCEL = "application/vnd.ms-excel";
//    private static final String MIME_TYPE__POWERPOINT = "application/vnd.ms-powerpoint";
//    private static final String MIME_TYPE__PDF = "application/pdf";

    private final static Logger log = Logger.getLogger(IndexDocumentFactory.class.getName());
    Document indexDocument;
    Tika tikaAutodetect;
    Tika tikaHtml;

    IndexDocumentAdaptingVisitor(Document indexDocument, Tika tikaAutodetect, Tika tikaHtml) {
        this.indexDocument = indexDocument;
        this.tikaAutodetect = tikaAutodetect;
        this.tikaHtml = tikaHtml;
    }

    public void visitTextDocument(TextDocumentDomainObject textDocument) {
        indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__TEMPLATE, textDocument.getTemplateName()));

        for (Map.Entry<Integer, TextDomainObject> textEntry : textDocument.getTexts().entrySet()) {
            Integer textIndex = textEntry.getKey();
            TextDomainObject text = textEntry.getValue();
            indexDocument.add(new TextField(DocumentIndex.FIELD__NONSTRIPPED_TEXT, text.getText(), Field.Store.NO));
            String htmlStrippedText = stripHtml(textDocument, text);
            indexDocument.add(new TextField(DocumentIndex.FIELD__TEXT, htmlStrippedText, Field.Store.NO));
            indexDocument.add(new TextField(DocumentIndex.FIELD__TEXT + textIndex, htmlStrippedText, Field.Store.NO));

            indexDocument.add(new SortedDocValuesField(DocumentIndex.FIELD__TEXT + textIndex, new BytesRef(htmlStrippedText)));
        }

        for (MenuDomainObject menu : textDocument.getMenus().values()) {
            for (MenuItemDomainObject menuItem : menu.getMenuItems()) {
                indexDocument.add(new StringField(DocumentIndex.FIELD__CHILD_ID, "" + menuItem.getDocumentId(), Field.Store.YES));
            }
        }

        for (ImageDomainObject image : textDocument.getImages().values()) {
            String imageLinkUrl = image.getLinkUrl();
            if (null != imageLinkUrl && imageLinkUrl.length() > 0) {
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

    private String stripHtml(TextDocumentDomainObject textDocument, TextDomainObject text) {
        String string = text.getText();

        if (text.getType() != TextDomainObject.TEXT_TYPE_HTML) {
            InputStream in = IOUtils.toInputStream(string);
            try {
                String stripped = tikaHtml.parseToString(in);
                log.trace(String.format("Text doc id: %d. Stripped html to plain text: '%s' -> '%s'", textDocument.getId(), string, stripped));
                string = stripped;
            } catch (Exception e) {
                log.error(String.format("Text doc id: %d. Unable to strip html '%s'", textDocument.getId(), string), e);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }

        return string;
    }

    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        FileDocumentDomainObject.FileDocumentFile file = fileDocument.getDefaultFile();
        if (null == file) {
            return;
        }

        String ext = StringUtils.trimToEmpty(FilenameUtils.getExtension(file.getFilename())).toLowerCase();
        String mime = StringUtils.trimToEmpty(file.getMimeType()).toLowerCase();
        Config config = Imcms.getServices().getConfig();

        if (config.getIndexDisabledFileExtensionsAsSet().contains(ext)
                || config.getIndexDisabledFileMimesAsSet().contains(mime))
        {
            log.info(String.format("File [%s] will not be indexed. Index is disabled for filename extension [%s] or MIME [%s].", file.getFilename(), ext, mime));
        } else {
            indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__MIME_TYPE, mime));
            indexFileContents(fileDocument, file);
        }
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

    private void indexFileContents(FileDocumentDomainObject fileDocument, FileDocumentDomainObject.FileDocumentFile file) {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_DISPOSITION, file.getFilename());
        metadata.set(Metadata.CONTENT_TYPE, file.getMimeType());

        InputStream in = null;
        try {
            in = file.getInputStreamSource().getInputStream();
            String content = tikaAutodetect.parseToString(in);
            indexDocument.add(new TextField(DocumentIndex.FIELD__TEXT, content, Field.Store.NO));
        } catch (Exception e) {
            log.error(String.format("File doc id: %d. Unable to index content of file-doc-file '%s'", fileDocument.getId(), file), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}