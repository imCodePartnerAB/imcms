package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.jpa.doc.*;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;
import imcode.server.user.RoleId;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads documents from the database.
 */
@Component
public class DocumentLoader {

    /**
     * Permission to create child documents.
     */
    public final static int PERM_CREATE_DOCUMENT = 8;

    @Inject
    private DocRepository docRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private VersionRepository versionRepository;

    @Inject
    private MetaRepository metaRepository;

    @Inject
    private CommonContentRepository commonContentRepository;

    @Inject
    private DocumentLanguageMapper languageMapper;

    @Inject
    private DocumentContentMapper contentMapper;

    @Inject
    private DocumentContentInitializingVisitor documentContentInitializingVisitor;

    /**
     * Loads document's meta.
     *
     * @param docId document id.
     * @return loaded meta of null if meta with given id does not exists.
     */
    public DocumentMeta loadMeta(int docId) {
        return toDomainObject(metaRepository.findOne(docId));
    }

    /**
     * Loads and initializes document's content.
     */
    public <T extends DocumentDomainObject> T loadAndInitContent(T document) {
        DocumentCommonContent dcc = contentMapper.getCommonContent(document.getRef());

        document.setCommonContent(dcc != null
                ? dcc
                : DocumentCommonContent.builder().headline("").menuImageURL("").menuText("").build()
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

        for (Map.Entry<Integer, Integer> roleIdToPermissionSetId : jpaMeta.getRoleIdToPermissionSetIdMap().entrySet()) {
            rolePermissionMappings.setPermissionSetTypeForRole(
                    new RoleId(roleIdToPermissionSetId.getKey()),
                    DocumentPermissionSetTypeDomainObject.fromInt(roleIdToPermissionSetId.getValue()));
        }

        metaDO.setRoleIdToDocumentPermissionSetTypeMappings(rolePermissionMappings);
    }

    private void initDocumentsPermissionSets(DocumentMeta metaDO, Meta ormMeta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                ormMeta.getPermissionSetBitsMap(), ormMeta.getPermissionSetEx());

        metaDO.setPermissionSets(permissionSets);
    }

    private void initDocumentsPermissionSetsForNew(DocumentMeta metaDO, Meta jpaMeta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                jpaMeta.getPermissionSetBitsForNewMap(), jpaMeta.getPermissionSetExForNew());

        metaDO.setPermissionSetsForNewDocument(permissionSets);
    }

    private DocumentPermissionSets createDocumentsPermissionSets(
            Map<Integer, Integer> permissionSetBitsMap,
            Set<Meta.PermissionSetEx> permissionSetEx) {

        DocumentPermissionSets permissionSets = new DocumentPermissionSets();

        for (Map.Entry<Integer, Integer> permissionSetBitsEntry : permissionSetBitsMap.entrySet()) {
            Integer setId = permissionSetBitsEntry.getKey();
            Integer permissionSetBits = permissionSetBitsEntry.getValue();
            DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);

            if (permissionSetBits != 0 && restricted.isEmpty()) {
                restricted.setFromBits(permissionSetBits);
            }
        }

        for (Meta.PermissionSetEx ex : permissionSetEx) {
            Integer setId = ex.getSetId();
            DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);

            setPermissionData(restricted, ex.getPermissionId(), ex.getPermissionData());
        }

        return permissionSets;
    }

    private void setPermissionData(DocumentPermissionSetDomainObject permissionSet, Integer permissionId, Integer permissionData) {
        if (null != permissionId) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) permissionSet;
            switch (permissionId) {
                case PERM_CREATE_DOCUMENT:
                    textDocumentPermissionSet.addAllowedDocumentTypeId(permissionData);
                    break;
                case ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE:
                    textDocumentPermissionSet.addAllowedTemplateGroupId(permissionData);
                    break;
                default:
            }
        }
    }

    private DocumentMeta toDomainObject(Meta meta) {
        if (meta == null) return null;

        DocumentMeta metaDO = new DocumentMeta();

        metaDO.setArchivedDatetime(meta.getArchivedDatetime());
        metaDO.setArchiverId(meta.getArchiverId());
        metaDO.setCategoryIds(meta.getCategoryIds());
        metaDO.setCreatedDatetime(meta.getCreatedDatetime());
        metaDO.setCreatorId(meta.getCreatorId());
        metaDO.setDefaultVersionNo(meta.getDefaultVersionNo());
        metaDO.setDisabledLanguageShowMode(DocumentMeta.DisabledLanguageShowMode.valueOf(meta.getDisabledLanguageShowMode().name()));
        metaDO.setDocumentType(meta.getDocumentType());

        Set<DocumentLanguage> apiLanguages = meta.getEnabledLanguages().stream()
                .map(jpaLanguage -> languageMapper.toApiObject(jpaLanguage))
                .collect(Collectors.toSet());

        metaDO.setEnabledLanguages(apiLanguages);
        metaDO.setId(meta.getId());
        metaDO.setKeywords(meta.getKeywords());
        metaDO.setLinkableByOtherUsers(meta.getLinkableByOtherUsers());
        metaDO.setLinkedForUnauthorizedUsers(meta.getLinkedForUnauthorizedUsers());
        metaDO.setModifiedDatetime(meta.getModifiedDatetime());
        metaDO.setActualModifiedDatetime(meta.getModifiedDatetime());
        //m.setPermissionSets(entity.getPermissionSets)
        //m.setPermissionSetsForNew(entity.getPermissionSetExForNew)
        //m.setPermissionSetsForNewDocuments(entity.getPermissionSetsForNewDocuments)
        metaDO.setProperties(meta.getProperties());
        metaDO.setPublicationEndDatetime(meta.getPublicationEndDatetime());
        metaDO.setDepublisherId(meta.getDepublisherId());
        metaDO.setPublicationStartDatetime(meta.getPublicationStartDatetime());
        metaDO.setPublicationStatus(publicationStatusFromInt(meta.getPublicationStatusInt()));
        metaDO.setPublisherId(meta.getPublisherId());
        metaDO.setRestrictedOneMorePrivilegedThanRestrictedTwo(meta.getRestrictedOneMorePrivilegedThanRestrictedTwo());
        //m.setRoleIdToDocumentPermissionSetTypeMappings()
        metaDO.setSearchDisabled(meta.getSearchDisabled());
        metaDO.setTarget(meta.getTarget());

        initRoleIdToPermissionSetIdMap(metaDO, meta);
        initDocumentsPermissionSets(metaDO, meta);
        initDocumentsPermissionSetsForNew(metaDO, meta);

        return metaDO;
    }

    public PropertyRepository getPropertyRepository() {
        return propertyRepository;
    }

    @SuppressWarnings("unused")
    public void setPropertyRepository(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public VersionRepository getVersionRepository() {
        return versionRepository;
    }

    /////////////// unused /////////////////

    public void setVersionRepository(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    @SuppressWarnings("unused")
    public DocRepository getDocRepository() {
        return docRepository;
    }

    @SuppressWarnings("unused")
    public void setDocRepository(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    @SuppressWarnings("unused")
    public MetaRepository getMetaRepository() {
        return metaRepository;
    }

    @SuppressWarnings("unused")
    public void setMetaRepository(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    @SuppressWarnings("unused")
    public CommonContentRepository getCommonContentRepository() {
        return commonContentRepository;
    }

    @SuppressWarnings("unused")
    public void setCommonContentRepository(CommonContentRepository commonContentRepository) {
        this.commonContentRepository = commonContentRepository;
    }

    @SuppressWarnings("unused")
    public DocumentContentInitializingVisitor getDocumentContentInitializingVisitor() {
        return documentContentInitializingVisitor;
    }

    @SuppressWarnings("unused")
    public void setDocumentContentInitializingVisitor(DocumentContentInitializingVisitor documentContentInitializingVisitor) {
        this.documentContentInitializingVisitor = documentContentInitializingVisitor;
    }
}