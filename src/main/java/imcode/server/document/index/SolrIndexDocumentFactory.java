package imcode.server.document.index;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.ImcmsServices;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings.Mapping;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.solr.DocumentContentIndexer;
import imcode.util.DateConstants;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Create solr index document from imcms document.
 */
public class SolrIndexDocumentFactory {

    private final Logger log = Logger.getLogger(getClass());

    private ImcmsServices imcmsServices;
    private CategoryMapper categoryMapper;
    private DocumentMapper documentMapper;
    private DocumentContentIndexer documentContentIndexBuilder;

    public SolrIndexDocumentFactory(ImcmsServices imcmsServices) {
        this.imcmsServices = imcmsServices;
        this.documentMapper = imcmsServices.getDocumentMapper();
        this.categoryMapper = imcmsServices.getCategoryMapper();
        this.documentContentIndexBuilder = new DocumentContentIndexer();
    }
    
    /**
     * Creates SolrInputDocument from default document.
     *
     * @return solr document to be added into index.
     */
    // TODO: refactor
    public SolrInputDocument createIndexDocument(int documentId, I18nLanguage language) {
        DocumentDomainObject document = documentMapper.getDefaultDocument(documentId, language);

        if (document == null) {
            throw new RuntimeException(String.format("Unable to index document - document does not exists. Doc id: %s.", documentId));
        }

        Collection<I18nMeta> i18nMetas = documentMapper.getI18nMetas(documentId);

        SolrInputDocument indexDocument = new SolrInputDocument();

        indexDocument.addField(DocumentIndex.FIELD__META_ID, "" + documentId);
//        indexDocument.add(new Field(DocumentIndex.FIELD__META_ID, "" + documentId, Field.Store.YES, Field.Index.NOT_ANALYZED));
//        indexDocument.addField(DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC, "" + documentId);
//        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC, NumberTools.longToString(documentId) ) );
        
        RoleIdToDocumentPermissionSetTypeMappings roleIdMappings = document.getRoleIdsMappedToDocumentPermissionSetTypes();
        for (Mapping mapping : roleIdMappings.getMappings()) {
            indexDocument.addField(DocumentIndex.FIELD__ROLE_ID, Integer.toString(mapping.getRoleId().intValue()));
//            indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__ROLE_ID, Integer.toString(mapping.getRoleId().intValue())) );
        }


        for (I18nMeta l: i18nMetas) {
            String headline = l.getHeadline();
            String menuText = l.getMenuText();

            indexDocument.addField(DocumentIndex.FIELD__META_HEADLINE, headline);
//            indexDocument.add(new Field(DocumentIndex.FIELD__META_HEADLINE, headline, Field.Store.NO, Field.Index.ANALYZED));
            indexDocument.addField(DocumentIndex.FIELD__META_HEADLINE_KEYWORD, headline);
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__META_HEADLINE_KEYWORD, headline));
            indexDocument.addField(DocumentIndex.FIELD__META_TEXT, menuText);
//            indexDocument.add(new Field(DocumentIndex.FIELD__META_TEXT, menuText, Field.Store.NO, Field.Index.ANALYZED));
        }

        indexDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, "" + document.getDocumentTypeId());
//        indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__DOC_TYPE_ID, "" + document.getDocumentTypeId()));
        indexDocument.addField(DocumentIndex.FIELD__CREATOR_ID, "" + document.getCreatorId());
//        indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CREATOR_ID, "" + document.getCreatorId()));
        if (null != document.getPublisherId()) {
            indexDocument.addField(DocumentIndex.FIELD__PUBLISHER_ID, "" + document.getPublisherId());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PUBLISHER_ID, "" + document.getPublisherId()));
        }

        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__CREATED_DATETIME, document.getCreatedDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__MODIFIED_DATETIME, document.getModifiedDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__ACTIVATED_DATETIME, document.getPublicationStartDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_START_DATETIME, document.getPublicationStartDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__PUBLICATION_END_DATETIME, document.getPublicationEndDatetime());
        addDateFieldToIndexDocument(documentId, indexDocument, DocumentIndex.FIELD__ARCHIVED_DATETIME, document.getArchivedDatetime());

        indexDocument.addField(DocumentIndex.FIELD__STATUS, "" + document.getPublicationStatus());
//        indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__STATUS, "" + document.getPublicationStatus()));

        for (Map.Entry<String, String> entry : document.getProperties().entrySet()) {
            indexDocument.addField(entry.getKey(), entry.getValue());
//            indexDocument.add(unStoredKeyword(entry.getKey(), entry.getValue()));
        }

        try {
            documentContentIndexBuilder.index(document, indexDocument);
        } catch (Exception re) {
            log.error("Error indexing document-type-specific data of document " + document.getId(), re);
        }

        Set categories = categoryMapper.getCategories(document.getCategoryIds());
        for (Iterator iterator = categories.iterator(); iterator.hasNext();) {
            CategoryDomainObject category = (CategoryDomainObject) iterator.next();
            indexDocument.addField(DocumentIndex.FIELD__CATEGORY, category.getName());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY, category.getName()));
            indexDocument.addField(DocumentIndex.FIELD__CATEGORY_ID, "" + category.getId());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY_ID, "" + category.getId()));
            CategoryTypeDomainObject categoryType = category.getType();
            indexDocument.addField(DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName()));
            indexDocument.addField(DocumentIndex.FIELD__CATEGORY_TYPE_ID, "" + categoryType.getId());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__CATEGORY_TYPE_ID, "" + categoryType.getId()));
        }

        Set documentKeywords = document.getKeywords();
        for (Iterator iterator = documentKeywords.iterator(); iterator.hasNext();) {
            String documentKeyword = (String) iterator.next();
            indexDocument.addField(DocumentIndex.FIELD__KEYWORD, documentKeyword);
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__KEYWORD, documentKeyword));
        }

        List<Integer[]> parentDocumentAndMenuIds = documentMapper.getParentDocumentAndMenuIdsForDocument(document);
        for (Integer[] pair : parentDocumentAndMenuIds) {
            Integer parentId = pair[0];
            Integer menuId = pair[1];

            indexDocument.addField(DocumentIndex.FIELD__PARENT_ID, parentId.toString());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PARENT_ID, parentId.toString()));
            indexDocument.addField(DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId);
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId));
        }
        
        boolean hasParents = !parentDocumentAndMenuIds.isEmpty();
        indexDocument.addField(DocumentIndex.FIELD__HAS_PARENTS, Boolean.toString(hasParents));
//        indexDocument.add( unStoredKeyword( DocumentIndex.FIELD__HAS_PARENTS, Boolean.toString(hasParents) ) );

        if (document.getAlias() != null) {
            indexDocument.addField(DocumentIndex.FIELD__ALIAS, document.getAlias());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__ALIAS, document.getAlias()));
        }

        Map<String, String> documentProperties = document.getProperties();
        for (Map.Entry<String, String> propertyEntry : documentProperties.entrySet()) {
            indexDocument.addField(DocumentIndex.FIELD__PROPERTY_PREFIX + propertyEntry.getKey(), "" + propertyEntry.getValue());
//            indexDocument.add(unStoredKeyword(DocumentIndex.FIELD__PROPERTY_PREFIX + propertyEntry.getKey(), "" + propertyEntry.getValue()));
        }

        return indexDocument;
    }

    private void addDateFieldToIndexDocument(int documentId, SolrInputDocument indexDocument, String fieldName,
            Date date) {
        if (null != date) {
            try {
                indexDocument.addField(fieldName, date);
//                indexDocument.add(unStoredKeyword(fieldName, date));
                return;
            } catch (RuntimeException re) {
                DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
                log.warn("Failed to index datetime '" + dateFormat.format(date) + "' in field '" + fieldName + "' of document " + documentId, re);
            }
        }
//        indexDocument.addField(fieldName, "");
    }

//    static Field unStoredKeyword(String fieldName, String fieldValue) {
//        return new Field(fieldName, fieldValue.toLowerCase(), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
//    }
//
//    private static Field unStoredKeyword(String fieldName, Date fieldValue) {
//        return new Field(fieldName, DateTools.dateToString(fieldValue, DateTools.Resolution.MINUTE), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
//    }
    
}
