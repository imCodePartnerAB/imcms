package imcode.server.document.index;

import com.imcode.imcms.dao.ImageDao;
import com.imcode.imcms.dao.TextDao;
import imcode.server.Imcms;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SolrIndexDocumentAdaptingVisitor extends DocumentVisitor {

    private static final String MIME_TYPE__WORD = "application/msword";
    private static final String MIME_TYPE__EXCEL = "application/vnd.ms-excel";
    private static final String MIME_TYPE__POWERPOINT = "application/vnd.ms-powerpoint";
    private static final String MIME_TYPE__PDF = "application/pdf";

    SolrInputDocument indexDocument;

    SolrIndexDocumentAdaptingVisitor(SolrInputDocument indexDocument) {
        this.indexDocument = indexDocument;
    }

    /**
     * Texts and images are not taken from textDocument. Instead they are queried from DB.
     */
    // TODO: refactor and optimize
    public void visitTextDocument(TextDocumentDomainObject textDocument) {
        indexDocument.addField(DocumentIndex.FIELD__TEMPLATE, textDocument.getTemplateName());
//        indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__TEMPLATE, textDocument.getTemplateName()));

        TextDao textDao = (TextDao)Imcms.getSpringBean("textDao");
        List<TextDomainObject> texts = textDao.getTexts(textDocument.getId(), textDocument.getVersion().getNo());

        for (TextDomainObject text: texts) {
            Integer textIndex = text.getNo();
            indexDocument.addField(DocumentIndex.FIELD__NONSTRIPPED_TEXT, text.getText());
//            indexDocument.add(new Field(DocumentIndex.FIELD__NONSTRIPPED_TEXT, text.getText(), Field.Store.NO, Field.Index.ANALYZED));
            String htmlStrippedText = stripHtml(text);
            indexDocument.addField(DocumentIndex.FIELD__TEXT, htmlStrippedText);
//            indexDocument.add(new Field(DocumentIndex.FIELD__TEXT, htmlStrippedText, Field.Store.NO, Field.Index.ANALYZED));
            indexDocument.addField(DocumentIndex.FIELD__TEXT + textIndex, htmlStrippedText);
//            indexDocument.add(new Field(DocumentIndex.FIELD__TEXT + textIndex, htmlStrippedText, Field.Store.NO, Field.Index.ANALYZED));
	    }

        boolean hasChildren = false;
        for ( MenuDomainObject menu : textDocument.getMenus().values() ) {
            for ( MenuItemDomainObject menuItem : menu.getMenuItems() ) {
                hasChildren = true;
                indexDocument.addField(DocumentIndex.FIELD__CHILD_ID, "" + menuItem.getDocumentId());
//                indexDocument.add(new Field(DocumentIndex.FIELD__CHILD_ID, ""+menuItem.getDocumentId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            }
        }

        indexDocument.addField(DocumentIndex.FIELD__HAS_CHILDREN, Boolean.toString(hasChildren));
//        indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__HAS_CHILDREN, Boolean.toString(hasChildren)));

        ImageDao imageDao = (ImageDao)Imcms.getSpringBean("imageDao");
        List<ImageDomainObject> images = imageDao.getImages(textDocument.getId(), textDocument.getVersion().getNo());

        for (ImageDomainObject image: images) {
            String imageLinkUrl = image.getLinkUrl();
            if ( null != imageLinkUrl && imageLinkUrl.length() > 0 ) {
                indexDocument.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl);
//                indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl));
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
        indexDocument.addField(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType());
//        indexDocument.add(IndexDocumentFactory.unStoredKeyword(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType()));
        indexFileContents(file);
    }

    private final static Map EXTRACTORS = new HashMap(ArrayUtils.toMap(new Object[][] {
//            { MIME_TYPE__WORD, new MicrosoftWordTextExtractor() },
//            { MIME_TYPE__EXCEL, new MicrosoftExcelTextExtractor() },
//            { MIME_TYPE__POWERPOINT, new MicrosoftPowerPointTextExtractor() },
//            { MIME_TYPE__PDF, new PdfTextExtractor() },
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
            indexDocument.addField(DocumentIndex.FIELD__TEXT, text);
//            indexDocument.add(new Field(DocumentIndex.FIELD__TEXT, text, Field.Store.NO, Field.Index.ANALYZED));
        }
    }

}
