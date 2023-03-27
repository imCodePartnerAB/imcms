package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Loads documents from the database.
 */
@SuppressWarnings("WeakerAccess")
@Component
@Transactional
public class DocumentLoader {

    private final MetaRepository metaRepository;
    private final DocumentContentMapper contentMapper;
    private final DocumentContentInitializingVisitor documentContentInitializingVisitor;

    public DocumentLoader(MetaRepository metaRepository,
                          DocumentContentMapper contentMapper,
                          DocumentContentInitializingVisitor documentContentInitializingVisitor) {

        this.metaRepository = metaRepository;
        this.contentMapper = contentMapper;
        this.documentContentInitializingVisitor = documentContentInitializingVisitor;
    }

    /**
     * Loads document's meta.
     *
     * @param docId document id.
     * @return loaded meta of null if meta with given id does not exists.
     */
    public DocumentMeta loadMeta(int docId) {
	    return toDomainObject(metaRepository.findById(docId).orElse(null));
    }

    /**
     * Loads and initializes document's content.
     */
    public <T extends DocumentDomainObject> T loadAndInitContent(T document) {
        DocumentCommonContent dcc = contentMapper.getCommonContent(document.getRef());

        document.setCommonContent(dcc != null
		        ? dcc
		        : DocumentCommonContent.builder().alias("").headline("").menuText("").build()
        );
        document.accept(documentContentInitializingVisitor);

        return document;
    }

    private Document.PublicationStatus publicationStatusFromInt(int publicationStatusInt) {
        Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;
        if (Document.PublicationStatus.APPROVED.asInt() == publicationStatusInt) {
            publicationStatus = Document.PublicationStatus.APPROVED;
        } else if (Document.PublicationStatus.DISAPPROVED.asInt() == publicationStatusInt) {
            publicationStatus = Document.PublicationStatus.DISAPPROVED;
        }
        return publicationStatus;
    }

    // Moved from  DocumentInitializer.initDocuments
    private void initRoleIdToPermissionSetIdMap(DocumentMeta metaDO, Meta jpaMeta) {
        RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings =
                new RoleIdToDocumentPermissionSetTypeMappings();

        for (Map.Entry<Integer, Permission> roleIdToPermissionSetId : jpaMeta.getRoleIdToPermission().entrySet()) {
            rolePermissionMappings.setPermissionSetTypeForRole(
                    roleIdToPermissionSetId.getKey(),
                    roleIdToPermissionSetId.getValue());
        }

        metaDO.setRoleIdToDocumentPermissionSetTypeMappings(rolePermissionMappings);
    }

    private DocumentMeta toDomainObject(Meta meta) {
        if (meta == null) return null;

        DocumentMeta metaDO = new DocumentMeta();

        metaDO.setArchivedDatetime(meta.getArchivedDatetime());
        metaDO.setArchiverId(meta.getArchiverId());
        metaDO.setCategories(meta.getCategories());
        metaDO.setCreatedDatetime(meta.getCreatedDatetime());
        metaDO.setCreatorId(meta.getCreatorId());
	    metaDO.setDefaultVersionNo(meta.getDefaultVersionNo());
	    metaDO.setDefaultLanguageAliasEnabled(meta.isDefaultLanguageAliasEnabled());
	    metaDO.setDisabledLanguageShowMode(DocumentMeta.DisabledLanguageShowMode.valueOf(meta.getDisabledLanguageShowMode().name()));
        metaDO.setDocumentTypeId(meta.getDocumentType().ordinal());
        metaDO.setId(meta.getId());
        metaDO.setKeywords(meta.getKeywords());
        metaDO.setLinkableByOtherUsers(meta.getLinkableByOtherUsers());
        metaDO.setLinkedForUnauthorizedUsers(meta.getLinkedForUnauthorizedUsers());
        metaDO.setCacheForUnauthorizedUsers(meta.isCacheForUnauthorizedUsers());
        metaDO.setCacheForAuthorizedUsers(meta.isCacheForAuthorizedUsers());
        metaDO.setVisible(meta.getVisible());
        metaDO.setModifiedDatetime(meta.getModifiedDatetime());
        metaDO.setActualModifiedDatetime(meta.getModifiedDatetime());
        metaDO.setProperties(meta.getProperties());
        metaDO.setPublicationEndDatetime(meta.getPublicationEndDatetime());
        metaDO.setDepublisherId(meta.getDepublisherId());
        metaDO.setPublicationStartDatetime(meta.getPublicationStartDatetime());
        metaDO.setPublicationStatus(publicationStatusFromInt(meta.getPublicationStatus().ordinal()));
        metaDO.setPublisherId(meta.getPublisherId());
        metaDO.setSearchDisabled(meta.isSearchDisabled());
        metaDO.setTarget(meta.getTarget());
        metaDO.setRestrictedPermissions(meta.getRestrictedPermissions());
        metaDO.setDocumentWasteBasket(meta.getDocumentWasteBasket());

        initRoleIdToPermissionSetIdMap(metaDO, meta);

        return metaDO;
    }

}
