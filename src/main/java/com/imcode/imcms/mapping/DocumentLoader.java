package com.imcode.imcms.mapping;

import com.imcode.imcms.util.Factory;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.DocumentPermissionSets;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.user.RoleId;

import java.util.Map;
import java.util.Set;

import com.imcode.imcms.api.*;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.dao.DocumentVersionDao;

/**
 * Loads documents from the database.
 * <p/>
 * Instantiated by spring-framework and initialized in DocumentMapper constructor.
 */
public class DocumentLoader {

    /**
     * Permission to create child documents.
     */
    public final static int PERM_CREATE_DOCUMENT = 8;

    /**
     * Injected by spring.
     */
    private MetaDao metaDao;

    /**
     * Injected by spring.
     */
    private DocumentVersionDao documentVersionDao;

    /**
     * Initializes document's fields.
     */
    private DocumentInitializingVisitor documentInitializingVisitor;

    /**
     * Loads document's meta.
     *
     * @param docId document id.
     * @return
     */
    public Meta loadMeta(Integer docId) {
        Meta meta = metaDao.getMeta(docId);

        if (meta != null) {
            meta.setActualModifiedDatetime(meta.getModifiedDatetime());

            Document.PublicationStatus publicationStatus = publicationStatusFromInt(meta.getPublicationStatusInt());
            meta.setPublicationStatus(publicationStatus);

            initRoleIdToPermissionSetIdMap(meta);
            initDocumentsPermissionSets(meta);
            initDocumentsPermissionSetsForNew(meta);
        }

        return meta;
    }

    /**
     * Loads and initializes document's fields.
     *
     * @param meta
     * @param version
     * @param language
     * @return
     */
    public DocumentDomainObject loadAndInitDocument(Meta meta, DocumentVersion version, I18nLanguage language) {
        return initDocument(createDocument(meta, version, language));
    }


    /**
     * Creates document instance.
     */
    private DocumentDomainObject createDocument(Meta meta, DocumentVersion version, I18nLanguage language) {
        I18nMeta labels = metaDao.getI18nMeta(meta.getId(), language);

        if (labels == null) {
            labels = Factory.createI18nMeta(meta.getId(), language);
        }

        DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());

        document.setMeta(meta);
        document.setLanguage(language);
        document.setI18nMeta(labels);

        document.setVersion(version);

        return document;
    }

    /**
     * Initializes document's fields.
     * <p/>
     * TODO: Refactor out AOP aspects creation and copy-paste.
     */
    private DocumentDomainObject initDocument(DocumentDomainObject document) {
        if (document == null) return null;

        /*
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(document);
        aspectJProxyFactory.setProxyTargetClass(true);

        switch (document.loadMeta().getDocumentType()) {
        case DocumentTypeDomainObject.TEXT_ID:
            aspectJProxyFactory.addAspect(new TextDocumentLazyLoadingAspect(
                    documentInitializingVisitor.getTextDocumentInitializer()));
           break;

        case DocumentTypeDomainObject.FILE_ID:
            aspectJProxyFactory.addAspect(new FileDocumentLazyLoadingAspect(documentInitializingVisitor));
            break;

        case DocumentTypeDomainObject.URL_ID:
            aspectJProxyFactory.addAspect(new UrlDocumentLazyLoadingAspect(documentInitializingVisitor));
            break;

        case DocumentTypeDomainObject.HTML_ID:
            aspectJProxyFactory.addAspect(new HtmlDocumentLazyLoadingAspect(documentInitializingVisitor));
            break;

        default:
            throw new AssertionError("Unknown document type id: " + document.loadMeta().getDocumentType());
        }
        
        return aspectJProxyFactory.getProxy();
        */
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
    private void initRoleIdToPermissionSetIdMap(Meta meta) {
        RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings =
                new RoleIdToDocumentPermissionSetTypeMappings();

        for (Map.Entry<Integer, Integer> roleIdToPermissionSetId : meta.getRoleIdToPermissionSetIdMap().entrySet()) {
            rolePermissionMappings.setPermissionSetTypeForRole(
                    new RoleId(roleIdToPermissionSetId.getKey()),
                    DocumentPermissionSetTypeDomainObject.fromInt(roleIdToPermissionSetId.getValue()));
        }

        meta.setRoleIdsMappedToDocumentPermissionSetTypes(rolePermissionMappings);
    }

    private void initDocumentsPermissionSets(Meta meta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                meta.getPermissionSetBitsMap(), meta.getPermisionSetEx());

        meta.setPermissionSets(permissionSets);
    }


    private void initDocumentsPermissionSetsForNew(Meta meta) {
        DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
                meta.getPermissionSetBitsForNewMap(), meta.getPermisionSetExForNew());

        meta.setPermissionSetsForNew(permissionSets);
    }


    private DocumentPermissionSets createDocumentsPermissionSets(
            Map<Integer, Integer> permissionSetBitsMap,
            Set<Meta.PermisionSetEx> permissionSetEx) {

        DocumentPermissionSets permissionSets = new DocumentPermissionSets();

        for (Map.Entry<Integer, Integer> permissionSetBitsEntry : permissionSetBitsMap.entrySet()) {
            Integer setId = permissionSetBitsEntry.getKey();
            Integer permissionSetBits = permissionSetBitsEntry.getValue();
            DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);

            if (permissionSetBits != 0 && restricted.isEmpty()) {
                restricted.setFromBits(permissionSetBits);
            }
        }

        for (Meta.PermisionSetEx ex : permissionSetEx) {
            Integer setId = ex.getSetId();
            DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);

            setPermissionData(restricted, ex.getPermissionId(), ex.getPermissionData());
        }

        return permissionSets;
    }


    private void setPermissionData(DocumentPermissionSetDomainObject permissionSet, Integer permissionId, Integer permissionData) {
        if (null != permissionId) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) permissionSet;
            switch (permissionId.intValue()) {
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

    public void setDocumentInitializingVisitor(
            DocumentInitializingVisitor documentInitializingVisitor) {
        this.documentInitializingVisitor = documentInitializingVisitor;
    }

    public DocumentVersionDao getDocumentVersionDao() {
        return documentVersionDao;
    }

    public void setDocumentVersionDao(DocumentVersionDao documentVersionDao) {
        this.documentVersionDao = documentVersionDao;
    }
}