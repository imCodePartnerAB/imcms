package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.mapping.dao.DocVersionDao;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.orm.DocCommonContent;
import com.imcode.imcms.mapping.orm.DocMeta;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;
import imcode.server.user.RoleId;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

/**
 * Loads documents from the database.
 * <p/>
 * Instantiated by spring-framework and initialized in DocumentMapper constructor.
 */
@Service
public class DocumentLoader {

    /**
     * Permission to create child documents.
     */
    public final static int PERM_CREATE_DOCUMENT = 8;

    @Inject
    private MetaDao metaDao;

    @Inject
    private DocVersionDao documentVersionDao;

    /**
     * Initializes document's fields.
     */
    private DocumentInitializingVisitor documentInitializingVisitor;

    /**
     * Loads document's meta.
     *
     * @param docId document id.
     * @return loaded meta of null if meta with given id does not exists.
     */
    //fixme: alias
    public Meta loadMeta(int docId) {
        DocMeta ormMeta = metaDao.getMeta(docId);

        if (ormMeta == null) return null;

        Meta meta = OrmToApi.toApi(ormMeta);

        if (meta != null) {
            meta.setActualModifiedDatetime(meta.getModifiedDatetime());

            Document.PublicationStatus publicationStatus = publicationStatusFromInt(ormMeta.getPublicationStatusInt());
            meta.setPublicationStatus(publicationStatus);

            initRoleIdToPermissionSetIdMap(meta, ormMeta);
            initDocumentsPermissionSets(meta, ormMeta);
            initDocumentsPermissionSetsForNew(meta, ormMeta);
        }

        return meta;
    }

    /**
     * Loads and initializes document's content.
     */
    public <T extends DocumentDomainObject> T loadAndInitContent(T document) {
        DocCommonContent ormAppearance = metaDao.getDocAppearance(document.getRef());
        DocumentCommonContent appearance = ormAppearance != null
                ? OrmToApi.toApi(ormAppearance)
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
    private void initRoleIdToPermissionSetIdMap(Meta meta, DocMeta ormMeta) {
        RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings =
                new RoleIdToDocumentPermissionSetTypeMappings();

        for (Map.Entry<Integer, Integer> roleIdToPermissionSetId : ormMeta.getRoleIdToPermissionSetIdMap().entrySet()) {
            rolePermissionMappings.setPermissionSetTypeForRole(
                    new RoleId(roleIdToPermissionSetId.getKey()),
                    DocumentPermissionSetTypeDomainObject.fromInt(roleIdToPermissionSetId.getValue()));
        }

        meta.setRoleIdsMappedToDocumentPermissionSetTypes(rolePermissionMappings);
    }

    private void initDocumentsPermissionSets(Meta meta, DocMeta ormMeta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                ormMeta.getPermissionSetBitsMap(), ormMeta.getPermisionSetEx());

        meta.setPermissionSets(permissionSets);
    }


    private void initDocumentsPermissionSetsForNew(Meta meta, DocMeta ormMeta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                ormMeta.getPermissionSetBitsForNewMap(), ormMeta.getPermisionSetExForNew());

        meta.setPermissionSetsForNew(permissionSets);
    }


    private DocumentPermissionSets createDocumentsPermissionSets(
            Map<Integer, Integer> permissionSetBitsMap,
            Set<DocMeta.PermisionSetEx> permissionSetEx) {

        DocumentPermissionSets permissionSets = new DocumentPermissionSets();

        for (Map.Entry<Integer, Integer> permissionSetBitsEntry : permissionSetBitsMap.entrySet()) {
            Integer setId = permissionSetBitsEntry.getKey();
            Integer permissionSetBits = permissionSetBitsEntry.getValue();
            DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);

            if (permissionSetBits != 0 && restricted.isEmpty()) {
                restricted.setFromBits(permissionSetBits);
            }
        }

        for (DocMeta.PermisionSetEx ex : permissionSetEx) {
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


    public MetaDao getMetaDao() {
        return metaDao;
    }

    public void setMetaDao(MetaDao metaDao) {
        this.metaDao = metaDao;
    }

    public DocumentInitializingVisitor getDocumentInitializingVisitor() {
        return documentInitializingVisitor;
    }

    public void setDocumentInitializingVisitor(DocumentInitializingVisitor documentInitializingVisitor) {
        this.documentInitializingVisitor = documentInitializingVisitor;
    }

    public DocVersionDao getDocumentVersionDao() {
        return documentVersionDao;
    }

    public void setDocumentVersionDao(DocVersionDao documentVersionDao) {
        this.documentVersionDao = documentVersionDao;
    }
}