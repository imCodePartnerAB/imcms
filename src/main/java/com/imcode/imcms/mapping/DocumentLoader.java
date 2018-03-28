package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.user.RoleId;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Loads documents from the database.
 */
@Component
public class DocumentLoader {

    private final DocumentVersionMapper versionMapper;
    private final DocumentLanguages documentLanguages;
    private final MetaRepository metaRepository;
    private final DocumentContentMapper contentMapper;
    private final DocumentContentInitializingVisitor documentContentInitializingVisitor;

    public DocumentLoader(DocumentVersionMapper versionMapper,
                          DocumentLanguages documentLanguages,
                          MetaRepository metaRepository,
                          DocumentContentMapper contentMapper,
                          DocumentContentInitializingVisitor documentContentInitializingVisitor) {

        this.versionMapper = versionMapper;
        this.documentLanguages = documentLanguages;
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

    /**
     * @return custom doc or null if doc does not exists
     */
    public <T extends DocumentDomainObject> T getCustomDoc(DocRef docRef) {
        DocumentMeta meta = loadMeta(docRef.getId());

        if (meta == null) {
            return null;
        }

        DocumentVersionInfo versionInfo = versionMapper.getInfo(docRef.getId());
        DocumentVersion version = versionInfo.getVersion(docRef.getVersionNo());
        T doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());

        doc.setMeta(meta.clone());
        doc.setVersionNo(version.getNo());
        doc.setLanguage(documentLanguages.getByCode(docRef.getLanguageCode()));

        return loadAndInitContent(doc);
    }

    /**
     * @return default doc or null if doc does not exists
     */
    public <T extends DocumentDomainObject> T getDefaultDoc(int docId, String docLanguageCode) {
        DocumentMeta meta = loadMeta(docId);

        if (meta == null) {
            return null;
        }

        DocumentVersionInfo versionInfo = versionMapper.getInfo(docId);
        DocumentVersion version = versionInfo.getDefaultVersion();
        T doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());

        doc.setMeta(meta.clone());
        doc.setVersionNo(version.getNo());
        doc.setLanguage(documentLanguages.getByCode(docLanguageCode));

        return loadAndInitContent(doc);
    }

    /**
     * @return working doc or null if doc does not exists
     */
    public <T extends DocumentDomainObject> T getWorkingDoc(int docId, String docLanguageCode) {
        DocumentMeta meta = loadMeta(docId);

        if (meta == null) {
            return null;
        }

        DocumentVersionInfo versionInfo = versionMapper.getInfo(docId);
        DocumentVersion version = versionInfo.getWorkingVersion();
        T doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());

        doc.setMeta(meta.clone());
        doc.setVersionNo(version.getNo());
        doc.setLanguage(documentLanguages.getByCode(docLanguageCode));

        return loadAndInitContent(doc);
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
                    new RoleId(roleIdToPermissionSetId.getKey()),
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
        metaDO.setDisabledLanguageShowMode(DocumentMeta.DisabledLanguageShowMode.valueOf(meta.getDisabledLanguageShowMode().name()));
        metaDO.setDocumentType(meta.getDocumentType().ordinal());
        metaDO.setId(meta.getId());
        metaDO.setKeywords(meta.getKeywords());
        metaDO.setLinkableByOtherUsers(meta.getLinkableByOtherUsers());
        metaDO.setLinkedForUnauthorizedUsers(meta.getLinkedForUnauthorizedUsers());
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

        initRoleIdToPermissionSetIdMap(metaDO, meta);

        return metaDO;
    }

}
