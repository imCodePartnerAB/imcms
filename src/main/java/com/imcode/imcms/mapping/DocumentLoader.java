package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import com.imcode.imcms.mapping.jpa.doc.Meta;
import com.imcode.imcms.mapping.jpa.doc.MetaRepository;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;
import imcode.server.user.RoleId;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

/**
 * Loads documents from the database.
 * <p/>
 * Instantiated by spring-framework and initialized in DocumentMapper constructor.
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
    private DocVersionRepository docVersionRepository;

    @Inject
    private MetaRepository metaRepository;

    @Inject
    private CommonContentRepository commonContentRepository;

    @Inject
    private EntityConverter entityConverter;

    /**
     * Initializes document's fields.
     */
    @Inject
    private DocumentInitializingVisitor documentInitializingVisitor;

    /**
     * Loads document's meta.
     *
     * @param docId document id.
     * @return loaded meta of null if meta with given id does not exists.
     */
    public DocumentMeta loadMeta(int docId) {
        Meta ormMeta = metaRepository.findOne(docId);

        if (ormMeta == null) return null;

        DocumentMeta documentMeta = entityConverter.fromEntity(ormMeta);

        if (documentMeta != null) {
            documentMeta.setActualModifiedDatetime(documentMeta.getModifiedDatetime());

            Document.PublicationStatus publicationStatus = publicationStatusFromInt(ormMeta.getPublicationStatusInt());
            documentMeta.setPublicationStatus(publicationStatus);

            initRoleIdToPermissionSetIdMap(documentMeta, ormMeta);
            initDocumentsPermissionSets(documentMeta, ormMeta);
            initDocumentsPermissionSetsForNew(documentMeta, ormMeta);
        }

        return documentMeta;
    }

    /**
     * Loads and initializes document's content.
     */
    public <T extends DocumentDomainObject> T loadAndInitContent(T document) {
        CommonContent ormAppearance = commonContentRepository.findByDocIdAndLanguageCode(document.getId(), document.getLanguage().getCode());
        DocumentCommonContent appearance = ormAppearance != null
                ? entityConverter.fromEntity(ormAppearance)
                : DocumentCommonContent.builder().headline("").menuImageURL("").menuText("").build();

        document.setCommonContent(appearance);
        document.accept(documentInitializingVisitor);

        return document;
    }


    private Document.PublicationStatus publicationStatusFromInt(int publicationStatusInt) {
        Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;
        if (Document.STATUS_PUBLICATION_APPROVED == publicationStatusInt) {
            publicationStatus = Document.PublicationStatus.APPROVED;
        } else if (Document.STATUS_PUBLICATION_DISAPPROVED == publicationStatusInt) {
            publicationStatus = Document.PublicationStatus.DISAPPROVED;
        }
        return publicationStatus;
    }

    // Moved from  DocumentInitializer.initDocuments
    private void initRoleIdToPermissionSetIdMap(DocumentMeta documentMeta, Meta ormMeta) {
        RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings =
                new RoleIdToDocumentPermissionSetTypeMappings();

        for (Map.Entry<Integer, Integer> roleIdToPermissionSetId : ormMeta.getRoleIdToPermissionSetIdMap().entrySet()) {
            rolePermissionMappings.setPermissionSetTypeForRole(
                    new RoleId(roleIdToPermissionSetId.getKey()),
                    DocumentPermissionSetTypeDomainObject.fromInt(roleIdToPermissionSetId.getValue()));
        }

        documentMeta.setRoleIdToDocumentPermissionSetTypeMappings(rolePermissionMappings);
    }

    private void initDocumentsPermissionSets(DocumentMeta documentMeta, Meta ormMeta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                ormMeta.getPermissionSetBitsMap(), ormMeta.getPermissionSetEx());

        documentMeta.setPermissionSets(permissionSets);
    }


    private void initDocumentsPermissionSetsForNew(DocumentMeta documentMeta, Meta ormMeta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                ormMeta.getPermissionSetBitsForNewMap(), ormMeta.getPermissionSetExForNew());

        documentMeta.setPermissionSetsForNewDocument(permissionSets);
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
                    textDocumentPermissionSet.addAllowedDocumentTypeId(permissionData.intValue());
                    break;
                case ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE:
                    textDocumentPermissionSet.addAllowedTemplateGroupId(permissionData.intValue());
                    break;
                default:
            }
        }
    }


    public DocRepository getDocRepository() {
        return docRepository;
    }

    public void setDocRepository(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    public DocVersionRepository getDocVersionRepository() {
        return docVersionRepository;
    }

    public void setDocVersionRepository(DocVersionRepository docVersionRepository) {
        this.docVersionRepository = docVersionRepository;
    }

    public MetaRepository getMetaRepository() {
        return metaRepository;
    }

    public void setMetaRepository(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    public CommonContentRepository getCommonContentRepository() {
        return commonContentRepository;
    }

    public void setCommonContentRepository(CommonContentRepository commonContentRepository) {
        this.commonContentRepository = commonContentRepository;
    }

    public DocumentInitializingVisitor getDocumentInitializingVisitor() {
        return documentInitializingVisitor;
    }

    public void setDocumentInitializingVisitor(DocumentInitializingVisitor documentInitializingVisitor) {
        this.documentInitializingVisitor = documentInitializingVisitor;
    }
}