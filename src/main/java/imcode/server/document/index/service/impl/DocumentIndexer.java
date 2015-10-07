package imcode.server.document.index.service.impl;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.Value;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.index.DocumentIndex;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 */
// todo: ??? Truncate date fields to minute ???
public class DocumentIndexer {

    private final Logger logger = Logger.getLogger(getClass());

    private DocumentMapper documentMapper;
    private CategoryMapper categoryMapper;
    private DocumentContentIndexer contentIndexer;

    public DocumentIndexer() {
    }

    public DocumentIndexer(DocumentMapper documentMapper, CategoryMapper categoryMapper, DocumentContentIndexer contentIndexer) {
        this.documentMapper = documentMapper;
        this.categoryMapper = categoryMapper;
        this.contentIndexer = contentIndexer;
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public void setDocumentMapper(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    public CategoryMapper getCategoryMapper() {
        return categoryMapper;
    }

    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public DocumentContentIndexer getContentIndexer() {
        return contentIndexer;
    }

    public void setContentIndexer(DocumentContentIndexer contentIndexer) {
        this.contentIndexer = contentIndexer;
    }

    /**
     * Creates SolrInputDocument based on provided DocumentDomainObject.
     *
     * @return SolrInputDocument
     */
    public SolrInputDocument index(DocumentDomainObject doc) {
        SolrInputDocument indexDoc = new SolrInputDocument();
        BiConsumer<String, Object> addFieldIfNotNull = (name, value) -> {
            if (value != null) indexDoc.addField(name, value);
        };


        int docId = doc.getId();
        String languageCode = doc.getLanguage().getCode();

        indexDoc.addField(DocumentIndex.FIELD__ID, String.format("%d_%s", docId, languageCode));
        indexDoc.addField(DocumentIndex.FIELD__TIMESTAMP, new Date());
        indexDoc.addField(DocumentIndex.FIELD__META_ID, docId);
        indexDoc.addField(DocumentIndex.FIELD__VERSION_NO, doc.getVersionNo());
        indexDoc.addField(DocumentIndex.FIELD__LANGUAGE_CODE, languageCode);
        indexDoc.addField(DocumentIndex.FIELD__SEARCH_ENABLED, !doc.isSearchDisabled());

        Value.with(doc.getCommonContent(), l -> {
            String headline = l.getHeadline();
            String menuText = l.getMenuText();

            indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE, headline);
            indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE_KEYWORD, headline);
            indexDoc.addField(DocumentIndex.FIELD__META_TEXT, menuText);
        });


        indexDoc.addField(DocumentIndex.FIELD__DOC_TYPE_ID, doc.getDocumentTypeId());
        indexDoc.addField(DocumentIndex.FIELD__CREATOR_ID, doc.getCreatorId());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__PUBLISHER_ID, doc.getPublisherId());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__CREATED_DATETIME, doc.getCreatedDatetime());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__MODIFIED_DATETIME, doc.getModifiedDatetime());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__ACTIVATED_DATETIME, doc.getPublicationStartDatetime());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, doc.getPublicationStartDatetime());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, doc.getPublicationEndDatetime());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__ARCHIVED_DATETIME, doc.getArchivedDatetime());

        indexDoc.addField(DocumentIndex.FIELD__STATUS, doc.getPublicationStatus().asInt());

        categoryMapper.getCategories(doc.getCategoryIds()).forEach(category -> {
            indexDoc.addField(DocumentIndex.FIELD__CATEGORY, category.getName());
            indexDoc.addField(DocumentIndex.FIELD__CATEGORY_ID, category.getId());

            Value.with(category.getType(), categoryType -> {
                indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName());
                indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE_ID, categoryType.getId());

            });
        });

        doc.getKeywords().forEach(documentKeyword -> indexDoc.addField(DocumentIndex.FIELD__KEYWORD, documentKeyword));

        List parentDocumentAndMenuIds = documentMapper.getParentDocumentAndMenuIdsForDocument(doc);

        parentDocumentAndMenuIds.forEach(it -> {
                    Object[] tuple = (Object[]) it;
                    int parentId = (int) tuple[0];
                    int menuId = (int) tuple[1];
                    indexDoc.addField(DocumentIndex.FIELD__PARENT_ID, parentId);
                    indexDoc.addField(DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId);
                }
        );

        indexDoc.addField(DocumentIndex.FIELD__HAS_PARENTS, !parentDocumentAndMenuIds.isEmpty());
        indexDoc.addField(DocumentIndex.FIELD__PARENTS_COUNT, parentDocumentAndMenuIds.size());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__ALIAS, doc.getAlias());

        doc.getProperties().forEach((key, value) -> indexDoc.addField(DocumentIndex.FIELD__PROPERTY_PREFIX + key, value));

        RoleIdToDocumentPermissionSetTypeMappings roleIdMappings = doc.getRoleIdsMappedToDocumentPermissionSetTypes();
        for (RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping : roleIdMappings.getMappings()) {
            indexDoc.addField(DocumentIndex.FIELD__ROLE_ID, mapping.getRoleId().intValue());
        }

        try {
            contentIndexer.index(doc, indexDoc);
        } catch (Exception e) {
            logger.error(String.format("Failed to index doc's content. Doc id: %d, language: %s, type: %s",
                    docId, doc.getLanguage(), doc.getDocumentType()), e);
        }

        return indexDoc;
    }

}