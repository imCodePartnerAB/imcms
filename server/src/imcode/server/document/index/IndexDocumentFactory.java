package imcode.server.document.index;

import imcode.server.Imcms;
import imcode.server.document.*;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.util.DateConstants;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class IndexDocumentFactory {

    private final static Logger log = Logger.getLogger( IndexDocumentFactory.class.getName() );

    public Document createIndexDocument( DocumentDomainObject document ) {
        Document indexDocument = new Document();

        int documentId = document.getId();
        indexDocument.add( Field.Keyword( DocumentIndex.FIELD__META_ID, "" + documentId ) );
        indexDocument.add( Field.UnStored( DocumentIndex.FIELD__META_HEADLINE, document.getHeadline() ) );
        indexDocument.add( Field.UnStored( DocumentIndex.FIELD__META_TEXT, document.getMenuText() ) );
        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__DOC_TYPE_ID, "" + document.getDocumentTypeId() ) );
        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__CREATOR_ID, "" + document.getCreator().getId()) );
        if ( null != document.getPublisher() ){
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__PUBLISHER_ID, "" + document.getPublisher().getId()) );
        }
        SectionDomainObject[] sections = document.getSections();
        for ( int i = 0; i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__SECTION, section.getName() ) );
        }

        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__CREATED_DATETIME, document.getCreatedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__MODIFIED_DATETIME, document.getModifiedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__ACTIVATED_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_START_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_END_DATETIME, document.getPublicationEndDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__ARCHIVED_DATETIME, document.getArchivedDatetime() );

        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__STATUS, "" + document.getStatus() ) );

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

        try {
            document.accept( new IndexDocumentAdaptingVisitor( indexDocument ) );
        } catch (RuntimeException re) {
            log.error( "Error indexing document-type-specific data of document "+document.getId(), re) ;
        }

        CategoryDomainObject[] categories = document.getCategories();
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__CATEGORY_ID, "" + category.getId() ) );
        }

        String[] documentKeywords = document.getKeywords();
        for ( int i = 0; i < documentKeywords.length; i++ ) {
            String documentKeyword = documentKeywords[i];
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__KEYWORD, documentKeyword ) );
        }

        String[][] parentDocumentAndMenuIds = documentMapper.getParentDocumentAndMenuIdsForDocument( document );
        for ( int i = 0; i < parentDocumentAndMenuIds.length; i++ ) {
            String parentId = parentDocumentAndMenuIds[i][0];
            String menuId = parentDocumentAndMenuIds[i][1];
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__PARENT_ID, parentId ) );
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId ) );
        }

        return indexDocument;
    }

    private void addDateFieldToIndexDocument( int documentId, Document indexDocument, String fieldName,
                                              Date date ) {
        if ( null != date ) {
            try {
                indexDocument.add( unStoredKeyword( fieldName, date ) );
            } catch ( RuntimeException re ) {
                DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING );
                log.warn( "Failed to index datetime '" + dateFormat.format( date ) + "' in field '" + fieldName
                          + "' of document "
                          + documentId, re );
            }
        }
    }

    private static Date truncateDateToMinutePrecision( Date fieldValue ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( fieldValue );
        calendar.set( Calendar.MILLISECOND, 0 );
        calendar.set( Calendar.SECOND, 0 );
        Date truncatedDate = calendar.getTime();
        return truncatedDate;
    }

    private static Field unStoredKeyword( String fieldName, String fieldValue ) {
        return new Field( fieldName, fieldValue.toLowerCase(), false, true, false );
    }

    private static Field unStoredKeyword( String fieldName, Date fieldValue ) {
        Date truncatedDate = truncateDateToMinutePrecision( fieldValue );
        return new Field( fieldName, DateField.dateToString( truncatedDate ), false, true, false );
    }

    private static class IndexDocumentAdaptingVisitor extends DocumentVisitor {

        Document indexDocument;

        private IndexDocumentAdaptingVisitor( Document indexDocument ) {
            this.indexDocument = indexDocument;
        }

        public void visitTextDocument( TextDocumentDomainObject textDocument ) {
            Iterator textsIterator = textDocument.getTexts().entrySet().iterator();
            while ( textsIterator.hasNext() ) {
                Map.Entry textEntry = (Map.Entry)textsIterator.next();
                Integer textIndex = (Integer)textEntry.getKey();
                TextDomainObject text = (TextDomainObject)textEntry.getValue();
                indexDocument.add( Field.UnStored( DocumentIndex.FIELD__NONSTRIPPED_TEXT, text.getText() ) ) ;
                String htmlStrippedText = stripHtml(text) ;
                indexDocument.add( Field.UnStored( DocumentIndex.FIELD__TEXT, htmlStrippedText ) );
                indexDocument.add( Field.UnStored( DocumentIndex.FIELD__TEXT + textIndex, htmlStrippedText ) );
            }

            Iterator imagesIterator = textDocument.getImages().values().iterator();
            while ( imagesIterator.hasNext() ) {
                ImageDomainObject image = (ImageDomainObject)imagesIterator.next();
                String imageLinkUrl = image.getLinkUrl();
                if ( null != imageLinkUrl && imageLinkUrl.length() > 0 ) {
                    indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl ) );
                }
            }
        }

        private String stripHtml( TextDomainObject text ) {
            String string = text.getText() ;
            if (TextDomainObject.TEXT_TYPE_HTML == text.getType()) {
                string = string.replaceAll( "<[^>]+?>", "" ) ;
            }
            return string ;
        }

        public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
            FileDocumentDomainObject.FileDocumentFile file = fileDocument.getDefaultFile();
            if (null != file) {
                indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__MIME_TYPE, file.getMimeType() ) );
            }
        }
    }
}
