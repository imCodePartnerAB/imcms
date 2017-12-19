package imcode.server.document.index.service.impl;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.util.Value;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.index.DocumentIndex;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import java.util.Date;
import java.util.function.BiConsumer;

// todo: ??? Truncate date fields to minute ???
public class DocumentIndexer {

    private final Logger logger = Logger.getLogger(getClass());

    private CategoryMapper categoryMapper;
    private DocumentContentIndexer contentIndexer;

    public DocumentIndexer(CategoryMapper categoryMapper, DocumentContentIndexer contentIndexer) {
        this.categoryMapper = categoryMapper;
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