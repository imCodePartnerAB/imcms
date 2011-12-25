package imcode.server.document.index;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.SectionDomainObject;
import imcode.util.DateConstants;
import imcode.util.Utility;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.taglibs.standard.tei.DeclareTEI;
import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.html.HtmlParser;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

public class IndexDocumentFactory {

    private CategoryMapper categoryMapper;
    private Tika tikaAutodetect;
    private Tika tikaHtml;

    private final static Logger log = Logger.getLogger(IndexDocumentFactory.class.getName());

    public IndexDocumentFactory(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
        this.tikaAutodetect = new Tika();
        this.tikaAutodetect.setMaxStringLength(-1);

        HtmlParser parser = new HtmlParser();
        Detector detector = new Detector() {
            MediaType mediaType = MediaType.parse("text/html");
            public MediaType detect(InputStream input, Metadata metadata) {
                return mediaType;
            }
        };

        this.tikaHtml = new Tika(detector, parser);
        this.tikaHtml.setMaxStringLength(-1);
    }

    public Document createIndexDocument( DocumentDomainObject document ) {
        log.trace("Indexing document "+document.getId());
        Document indexDocument = new Document();

        int documentId = document.getId();
        indexDocument.add( Field.Keyword( DocumentIndex.FIELD__META_ID, "" + documentId ) );
        indexDocument.add( Field.UnStored( DocumentIndex.FIELD__META_HEADLINE, document.getHeadline() ) );
        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__META_HEADLINE_KEYWORD, document.getHeadline() ) );
        indexDocument.add( Field.UnStored( DocumentIndex.FIELD__META_TEXT, document.getMenuText() ) );
        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__DOC_TYPE_ID, "" + document.getDocumentTypeId() ) );
        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__CREATOR_ID, "" + document.getCreatorId()) );
        if ( null != document.getPublisherId() ){
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__PUBLISHER_ID, "" + document.getPublisherId()) );
        }
        Set sectionIds = document.getSectionIds();
        for ( Iterator iterator = sectionIds.iterator(); iterator.hasNext(); ) {
            Integer sectionId = (Integer) iterator.next();
            SectionDomainObject section = Imcms.getServices().getDocumentMapper().getSectionById(sectionId.intValue());
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__SECTION, section.getName() ) );
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__SECTION_ID, sectionId.toString())) ;
        }

        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__CREATED_DATETIME, document.getCreatedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__MODIFIED_DATETIME, document.getModifiedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__ACTIVATED_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_START_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_END_DATETIME, document.getPublicationEndDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, DocumentIndex.FIELD__ARCHIVED_DATETIME, document.getArchivedDatetime() );

        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__STATUS, "" + document.getPublicationStatus() ) );

        for ( Map.Entry<String, String> entry : document.getProperties().entrySet() ) {
            indexDocument.add(unStoredKeyword(entry.getKey(), entry.getValue()));
        }

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

        try {
            document.accept(new IndexDocumentAdaptingVisitor(indexDocument, tikaAutodetect, tikaHtml));
        } catch (RuntimeException re) {
            log.error( "Error indexing document-type-specific data of document "+document.getId(), re) ;
        }

        Set categories = categoryMapper.getCategories(document.getCategoryIds());
        for ( Iterator iterator = categories.iterator(); iterator.hasNext(); ) {
            CategoryDomainObject category = (CategoryDomainObject) iterator.next();
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

        if (document.getAlias() != null ) {
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__ALIAS, document.getAlias()));
        }

        Map<String, String> documentProperties = document.getProperties();
        for (Map.Entry<String, String> propertyEntry : documentProperties.entrySet()) {
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__PROPERTY_PREFIX +propertyEntry.getKey(), "" + propertyEntry.getValue() ) );
        }

        return indexDocument;
    }

    private void addDateFieldToIndexDocument( int documentId, Document indexDocument, String fieldName,
                                              Date date ) {
        if ( null != date ) {
            try {
                indexDocument.add( unStoredKeyword( fieldName, date ) );
                return ;
            } catch ( RuntimeException re ) {
                DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING );
                log.warn( "Failed to index datetime '" + dateFormat.format( date ) + "' in field '" + fieldName
                          + "' of document "
                          + documentId, re );
            }
        }
        indexDocument.add( unStoredKeyword( fieldName, "" ));
    }

    static Field unStoredKeyword( String fieldName, String fieldValue ) {
        return new Field( fieldName, fieldValue.toLowerCase(), false, true, false );
    }

    private static Field unStoredKeyword( String fieldName, Date fieldValue ) {
        Date truncatedDate = Utility.truncateDateToMinutePrecision( fieldValue );
        return new Field( fieldName, DateField.dateToString( truncatedDate ), false, true, false );
    }

}
