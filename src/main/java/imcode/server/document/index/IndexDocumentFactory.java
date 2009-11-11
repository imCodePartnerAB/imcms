package imcode.server.document.index;

import imcode.server.Imcms;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings.Mapping;
import imcode.util.DateConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumberTools;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;

/**
 * Create lucene index from document's fields.
 */
public class IndexDocumentFactory {
    private CategoryMapper categoryMapper;
    private final static Logger log = Logger.getLogger(IndexDocumentFactory.class.getName());

    public IndexDocumentFactory(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    
    /**
     * @return lucene document.
     */
    public Document createIndexDocument( DocumentDomainObject document ) {
        DocumentVersion version = document.getVersion();

        //log.debug(String.format("Indexing document %s, version %s (%s).",
        //        document.getId(), version.getTag(), version.getNumber()));
        
        Document indexDocument = new Document();

        int documentId = document.getId();
        indexDocument.add(new Field(DocumentIndex.FIELD__META_ID, "" + documentId, Field.Store.YES, Field.Index.NOT_ANALYZED));
        indexDocument.add(new Field(DocumentIndex.FIELD__VERSION_NUMBER, version.getNumber().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        //indexDocument.add(new Field(DocumentIndex.FIELD__VERSION_TAG, version.getTag().name(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC, NumberTools.longToString(documentId) ) );
        
        RoleIdToDocumentPermissionSetTypeMappings roleIdMappings = document.getRoleIdsMappedToDocumentPermissionSetTypes();
        for (Mapping mapping : roleIdMappings.getMappings()) {
            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__ROLE_ID, Integer.toString(mapping.getRoleId().intValue())) );
        }

        List<I18nLanguage> languages = I18nSupport.getLanguages();

        for (I18nLanguage language : languages) {
            String headline = document.getHeadline(language);
            String menuText = document.getMenuText(language);

            indexDocument.add(new Field(DocumentIndex.FIELD__META_HEADLINE, headline, Field.Store.NO, Field.Index.ANALYZED));
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__META_HEADLINE_KEYWORD, headline));

            indexDocument.add(new Field(DocumentIndex.FIELD__META_TEXT, menuText, Field.Store.NO, Field.Index.ANALYZED));
        }

        indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__DOC_TYPE_ID, "" + document.getDocumentTypeId()));
        indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CREATOR_ID, "" + document.getCreatorId()));
        if (null != document.getPublisherId()) {
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PUBLISHER_ID, "" + document.getPublisherId()));
        }

        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__CREATED_DATETIME, document.getCreatedDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__MODIFIED_DATETIME, document.getModifiedDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__ACTIVATED_DATETIME, document.getPublicationStartDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_START_DATETIME, document.getPublicationStartDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_END_DATETIME, document.getPublicationEndDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__ARCHIVED_DATETIME, document.getArchivedDatetime());

        indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__STATUS, "" + document.getPublicationStatus()));

        for (Map.Entry<String, String> entry : document.getProperties().entrySet()) {
            indexDocument.add(unStoredKeyword(entry.getKey(), entry.getValue()));
        }

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

        try {
            document.accept(new IndexDocumentAdaptingVisitor(indexDocument));
        } catch (RuntimeException re) {
            log.error("Error indexing document-type-specific data of document " + document.getId(), re);
        }

        Set categories = categoryMapper.getCategories(document.getCategoryIds());
        for (Iterator iterator = categories.iterator(); iterator.hasNext();) {
            CategoryDomainObject category = (CategoryDomainObject) iterator.next();
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY, category.getName()));
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY_ID, "" + category.getId()));
            CategoryTypeDomainObject categoryType = category.getType();
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName()));
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY_TYPE_ID, "" + categoryType.getId()));
        }

        for (I18nLanguage language : languages) {
            Set documentKeywords = document.getKeywords(language);
            for (Iterator iterator = documentKeywords.iterator(); iterator.hasNext();) {
                String documentKeyword = (String) iterator.next();
                indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__KEYWORD, documentKeyword));
            }
        }

        List<Integer[]> parentDocumentAndMenuIds = documentMapper.getParentDocumentAndMenuIdsForDocument(document);
        for (Integer[] pair : parentDocumentAndMenuIds) {
            Integer parentId = pair[0];
            Integer menuId = pair[1];
            
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PARENT_ID, parentId.toString()));
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId));
        }
        
        boolean hasParents = !parentDocumentAndMenuIds.isEmpty();
        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__HAS_PARENTS, Boolean.toString(hasParents) ) );

        if (document.getAlias() != null) {
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__ALIAS, document.getAlias()));
        }

        Map<String, String> documentProperties = document.getProperties();
        for (Map.Entry<String, String> propertyEntry : documentProperties.entrySet()) {
            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PROPERTY_PREFIX + propertyEntry.getKey(), "" + propertyEntry.getValue()));
        }

        return indexDocument;
    }

    private void addDateFieldToIndexDocument(int documentId, Document indexDocument, String fieldName,
            Date date) {
        if (null != date) {
            try {
                indexDocument.add(unStoredKeyword(fieldName, date));
                return;
            } catch (RuntimeException re) {
                DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
                log.warn("Failed to index datetime '" + dateFormat.format(date) + "' in field '" + fieldName + "' of document " + documentId, re);
            }
        }
        indexDocument.add(unStoredKeyword(fieldName, ""));
    }

    static Field unStoredKeyword(String fieldName, String fieldValue) {
        return new Field(fieldName, fieldValue.toLowerCase(), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
    }

    private static Field unStoredKeyword(String fieldName, Date fieldValue) {
        return new Field(fieldName, DateTools.dateToString(fieldValue, DateTools.Resolution.MINUTE), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
    }
    
}
