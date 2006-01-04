package imcode.server.document.index;

import imcode.server.Imcms;
import imcode.server.document.*;
import com.imcode.imcms.mapping.DefaultDocumentMapper;
import imcode.util.DateConstants;
import imcode.util.Utility;
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
        log.trace("Indexing document "+document.getId());
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
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__SECTION_ID, ""+section.getId())) ;
        }

        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__CREATED_DATETIME, document.getCreatedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__MODIFIED_DATETIME, document.getModifiedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__ACTIVATED_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_START_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_END_DATETIME, document.getPublicationEndDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__ARCHIVED_DATETIME, document.getArchivedDatetime() );

        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__STATUS, "" + document.getPublicationStatus() ) );

        DefaultDocumentMapper documentMapper = Imcms.getServices().getDefaultDocumentMapper();

        try {
            document.accept( new IndexDocumentAdaptingVisitor( indexDocument ) );
        } catch (RuntimeException re) {
            log.error( "Error indexing document-type-specific data of document "+document.getId(), re) ;
        }

        CategoryDomainObject[] categories = document.getCategories();
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__CATEGORY, category.getName() )) ;
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__CATEGORY_ID, "" + category.getId() ) );
            CategoryTypeDomainObject categoryType = category.getType() ;
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName() )) ;
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__CATEGORY_TYPE_ID, ""+categoryType.getId() )) ;
        }

        Set documentKeywords = document.getKeywords();
        for ( Iterator iterator = documentKeywords.iterator(); iterator.hasNext(); ) {
            String documentKeyword = (String) iterator.next();
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

    static Field unStoredKeyword( String fieldName, String fieldValue ) {
        return new Field( fieldName, fieldValue.toLowerCase(), false, true, false );
    }

    private static Field unStoredKeyword( String fieldName, Date fieldValue ) {
        Date truncatedDate = Utility.truncateDateToMinutePrecision( fieldValue );
        return new Field( fieldName, DateField.dateToString( truncatedDate ), false, true, false );
    }

}
