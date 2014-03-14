package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.doc.*;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Used internally by DocumentMapper.
 */
@Service
public class DocumentSaver {

    private DocumentMapper documentMapper;

    @Inject
    private DocRepository docRepository;

    @Inject
    private VersionRepository versionRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private CommonContentRepository commonContentRepository;

    @Inject
    private MetaRepository metaRepository;
    
    @Inject
    private TextDocumentContentSaver textDocumentContentSaver;

    @Inject
    private DocumentContentMapper documentContentMapper;

    @Inject
    private DocumentVersionMapper versionMapper;

    private DocumentPermissionSetMapper documentPermissionSetMapper = new DocumentPermissionSetMapper();


    /**
     * Updates doc's last modified date time if it was not set explicitly.
     *
     * @param doc
     */
    public void updateModifiedDtIfNotSetExplicitly(DocumentDomainObject doc) {
        Date explicitlyModifiedDatetime = Utility.truncateDateToMinutePrecision(doc.getActualModifiedDatetime());
        Date modifiedDatetime = Utility.truncateDateToMinutePrecision(doc.getModifiedDatetime());
        boolean modifiedDatetimeUnchanged = explicitlyModifiedDatetime.equals(modifiedDatetime);

        if (modifiedDatetimeUnchanged) {
            doc.setModifiedDatetime(new Date());
        }
    }

    /**
     * Saves edited text-document text and non-saved enclosing content loop if any.
     * If text is enclosed into unsaved content loop then the loop must also exist in document.
     *
     * @throws IllegalStateException if a text refers non-existing content loop.
     */
    @Transactional
    public void saveText(TextDocTextContainer container, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveText(container, user);
        docRepository.touch(container.getDocVersionRef(), user);
    }

    @Transactional
    public void saveTexts(TextDocTextsContainer container, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveTexts(container, user);
        docRepository.touch(container.getVersionRef(), user);
    }

    @Transactional
    public void saveImages(TextDocImagesContainer container, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveImages(container, user);
        docRepository.touch(container.getVersionRef(), user);
    }

    @Transactional
    public void saveMenu(TextDocMenuContainer container, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveMenu(container, user);
        docRepository.touch(container.getVersionRef(), user);
    }

    @Transactional
    public void saveImage(TextDocImageContainer container, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveImage(container, user);
        docRepository.touch(container.getVersionRef(), user);
    }

    @Transactional
    public void changeDocumentDefaultVersion(int docId, int newDefaultVersionNo, UserDomainObject publisher) {
        Version currentDefaultVersion = versionRepository.findDefault(docId);

        if (currentDefaultVersion.getNo() != newDefaultVersionNo) {
            versionRepository.updateDefaultNo(docId, newDefaultVersionNo, publisher.getId());

            docRepository.touch(VersionRef.of(docId, newDefaultVersionNo), publisher);
        }
    }


    /**
     * @param docs
     * @param user
     * @return
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    @Transactional
    public DocumentVersion makeDocumentVersion(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentDomainObject firstDoc = docs.get(0);
        DocumentMeta meta = firstDoc.getMeta().clone();
        DocumentVersion nextVersion = versionMapper.create(meta.getId(), user.getId());
        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(nextVersion.getNo());

            documentContentMapper.saveCommonContent(doc, user);
        }

        // Currently only text doc has i18n content.
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(docCreatingVisitor);
        } else {
            textDocumentContentSaver.createSharedContent((TextDocumentDomainObject) firstDoc, user);

            for (DocumentDomainObject doc : docs) {
                textDocumentContentSaver.createI18nContent((TextDocumentDomainObject) doc, user);
            }
        }

        return nextVersion;
    }

    @Transactional
    public void updateDocument(DocumentDomainObject doc, Map<DocumentLanguage, DocumentCommonContent> appearances, DocumentDomainObject oldDoc,
                               UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        Meta jpaMeta = toJpaObject(doc.getMeta());

        if (user.canEditPermissionsFor(oldDoc)) {
            newUpdateDocumentRolePermissions(jpaMeta, doc, user, oldDoc);
            documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(jpaMeta, doc, user, oldDoc);
        }

        DocumentSavingVisitor savingVisitor = new DocumentSavingVisitor(oldDoc, documentMapper.getImcmsServices(), user);

        metaRepository.saveAndFlush(jpaMeta);

        for (Map.Entry<DocumentLanguage, DocumentCommonContent> e : appearances.entrySet()) {
            DocumentLanguage language = e.getKey();
            DocumentCommonContent dcc = e.getValue();
            CommonContent ormDcc = commonContentRepository.findByDocIdAndLanguageCode(doc.getId(), language.getCode());
            if (ormDcc == null) {
                ormDcc = new CommonContent();
            }

            ormDcc.setHeadline(dcc.getHeadline());
            ormDcc.setMenuImageURL(dcc.getMenuImageURL());
            ormDcc.setMenuText(dcc.getMenuText());

            if (ormDcc.getId() == null) {
                Language ormLanguage = languageRepository.findByCode(language.getCode());

                ormDcc.setDocId(doc.getId());
                ormDcc.setLanguage(ormLanguage);
                commonContentRepository.save(ormDcc);
            }
        }

        doc.accept(savingVisitor);
        updateModifiedDtIfNotSetExplicitly(doc);
        docRepository.touch(doc.getVersionRef(), user, doc.getModifiedDatetime());
    }


    /**
     * @param docs
     * @param user
     * @return
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    @Transactional
    public int saveNewDocsWithSharedMetaAndVersion(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentDomainObject firstDoc = docs.get(0);
        checkDocumentForSave(firstDoc);

        DocumentMeta meta = firstDoc.getMeta().clone();
        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());

        meta.setId(null);
        Meta jpaMeta = toJpaObject(meta);

        newUpdateDocumentRolePermissions(jpaMeta, firstDoc, user, null);

        // Update permissions
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(jpaMeta, firstDoc, user, null);

        int newDocId = metaRepository.saveAndFlush(jpaMeta).getId();

        docRepository.insertPropertyIfNotExists(newDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, Integer.toString(newDocId));

        Version copyVersion = versionRepository.create(newDocId, user.getId());
        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(copyVersion.getNo());

            documentContentMapper.saveCommonContent(doc, user);
        }

        // Currently only text docs contain non-common i18n content
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(docCreatingVisitor);
        } else {
            textDocumentContentSaver.createSharedContent((TextDocumentDomainObject) firstDoc, user);

            for (DocumentDomainObject doc : docs) {
                textDocumentContentSaver.createI18nContent((TextDocumentDomainObject) doc, user);
            }
        }

        return newDocId;
    }


    /**
     * Please note that custom (limited) permissions might be changed on save:
     * -If saving user is a super-admin or have full perms on a doc, then all custom perms settings are merely inherited.
     * -Otherwise custom (lim1 and lim2) perms are replaced with permissions set for new document.
     * <p/>
     * If user is a super-admin or has full permissions on a new document then
     *
     * @param doc
     * @param dccMap
     * @param saveOpts
     * @param user
     * @param <T>
     * @return
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    @Transactional
    public <T extends DocumentDomainObject> int saveNewDocument(T doc, Map<DocumentLanguage, DocumentCommonContent> dccMap,
                                                                EnumSet<DocumentMapper.SaveOpts> saveOpts, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        DocumentMeta metaDO = doc.getMeta().clone();
        documentMapper.setCreatedAndModifiedDatetimes(metaDO, new Date());
        metaDO.setId(null);
        metaDO.setDefaultVersionNo(DocumentVersion.WORKING_VERSION_NO);
        metaDO.setDocumentType(doc.getDocumentTypeId());

        if (!user.isSuperAdminOrHasFullPermissionOn(doc)) {
            metaDO.getPermissionSets().setRestricted1(metaDO.getPermissionSetsForNewDocument().getRestricted1());
            metaDO.getPermissionSets().setRestricted2(metaDO.getPermissionSetsForNewDocument().getRestricted2());
        }

        Meta jpaMeta = toJpaObject(metaDO);

        newUpdateDocumentRolePermissions(jpaMeta, doc, user, null);
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(jpaMeta, doc, user, null);

        int newDocId = metaRepository.saveAndFlush(jpaMeta).getId();

        for (Map.Entry<DocumentLanguage, DocumentCommonContent> entry : dccMap.entrySet()) {
            DocumentCommonContent dcc = entry.getValue();
            CommonContent jpaDcc = new CommonContent();
            Language jpaLanguage = languageRepository.findByCode(entry.getKey().getCode());

            jpaDcc.setDocId(newDocId);
            jpaDcc.setHeadline(dcc.getHeadline());
            jpaDcc.setMenuImageURL(dcc.getMenuImageURL());
            jpaDcc.setMenuText(dcc.getMenuText());
            jpaDcc.setLanguage(jpaLanguage);

            commonContentRepository.save(jpaDcc);
        }

        docRepository.insertPropertyIfNotExists(newDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, String.valueOf(newDocId));

        Version version = versionRepository.create(newDocId, user.getId());
        doc.setVersionNo(version.getNo());
        doc.setId(newDocId);

        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);
        doc.accept(docCreatingVisitor);

        if (doc instanceof TextDocumentDomainObject && saveOpts.contains(DocumentMapper.SaveOpts.CopyDocCommonContentIntoTextFields)) {
            Map<DocumentLanguage, TextDomainObject> texts1 = new HashMap<>();
            Map<DocumentLanguage, TextDomainObject> texts2 = new HashMap<>();

            for (Map.Entry<DocumentLanguage, DocumentCommonContent> entry : dccMap.entrySet()) {
                DocumentLanguage language = entry.getKey();
                DocumentCommonContent dcc = entry.getValue();

                texts1.put(language, new TextDomainObject(dcc.getHeadline()));
                texts2.put(language, new TextDomainObject(dcc.getMenuText()));
            }

            textDocumentContentSaver.saveTexts(
                    TextDocTextsContainer.of(VersionRef.of(version.getDocId(), version.getNo()), 1, texts1),
                    user
            );

            textDocumentContentSaver.saveTexts(
                    TextDocTextsContainer.of(VersionRef.of(version.getDocId(), version.getNo()), 2, texts2),
                    user
            );
        }

        return newDocId;
    }


    /**
     * Various non security checks.
     *
     * @param document
     * @throws NoPermissionInternalException
     * @throws DocumentSaveException
     */
    private void checkDocumentForSave(DocumentDomainObject document) throws NoPermissionInternalException, DocumentSaveException {
        documentMapper.getCategoryMapper().checkMaxDocumentCategoriesOfType(document);
        checkIfAliasAlreadyExist(document);
    }


    /**
     * Update meta roles to permissions set mapping.
     * Modified copy of legacy updateDocumentRolePermissions method.
     * NB! Compared to legacy this method does not update database.
     *
     * @param document    document being saved
     * @param user        an authorized user
     * @param oldDocument original doc when updating or null when inserting (a new doc)
     */
    private void newUpdateDocumentRolePermissions(Meta jpaMeta, DocumentDomainObject document, UserDomainObject user,
                                                  DocumentDomainObject oldDocument) {

        // Original (old) and modified or new document permission set type mapping.
        RoleIdToDocumentPermissionSetTypeMappings mappings = new RoleIdToDocumentPermissionSetTypeMappings();

        // Copy original document' roles to mapping with NONE(4) permissions-set assigned
        if (null != oldDocument) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping[] oldDocumentMappings = oldDocument.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings();
            for (RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping : oldDocumentMappings) {
                mappings.setPermissionSetTypeForRole(mapping.getRoleId(), DocumentPermissionSetTypeDomainObject.NONE);
            }
        }

        // Copy modified or new document' roles to mapping
        RoleIdToDocumentPermissionSetTypeMappings.Mapping[] documentMappings = document.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings();
        for (RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping : documentMappings) {
            mappings.setPermissionSetTypeForRole(mapping.getRoleId(), mapping.getDocumentPermissionSetType());
        }

        RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappingsArray = mappings.getMappings();

        Map<Integer, Integer> roleIdToPermissionSetIdMap = jpaMeta.getRoleIdToPermissionSetIdMap();

        for (RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping : mappingsArray) {
            RoleId roleId = mapping.getRoleId();
            DocumentPermissionSetTypeDomainObject documentPermissionSetType = mapping.getDocumentPermissionSetType();

            if (null == oldDocument
                    || user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(documentPermissionSetType, roleId, oldDocument)) {

                // According to schema design NONE value can not be save into the DB table
                if (documentPermissionSetType.equals(DocumentPermissionSetTypeDomainObject.NONE)) {
                    roleIdToPermissionSetIdMap.remove(roleId.intValue());
                } else {
                    roleIdToPermissionSetIdMap.put(roleId.intValue(), documentPermissionSetType.getId());
                }
            }
        }
    }

    private void checkIfAliasAlreadyExist(DocumentDomainObject document) throws AliasAlreadyExistsInternalException {
        String alias = document.getAlias();

        if (alias != null) {
            Property property = docRepository.getAliasProperty(alias);
            if (property != null) {
                Integer documentId = document.getId();

                if (!property.getDocId().equals(documentId)) {
                    throw new AliasAlreadyExistsInternalException(
                            String.format("Alias %s is already in use by document %d.", alias, documentId));
                }
            }
        }
    }

    // todo: check permission
    private Meta toJpaObject(DocumentMeta metaDO) {
        Meta meta = new Meta();

        meta.setArchivedDatetime(metaDO.getArchivedDatetime());
        meta.setCategoryIds(metaDO.getCategoryIds());
        meta.setCreatedDatetime(metaDO.getCreatedDatetime());
        meta.setCreatorId(metaDO.getCreatorId());
        meta.setDefaultVersionNo(metaDO.getDefaultVersionNo());
        meta.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.valueOf(metaDO.getDisabledLanguageShowMode().name()));
        meta.setDocumentType(metaDO.getDocumentType());

        Set<Language> enabledLanguages = new HashSet<>();

        for (DocumentLanguage l : metaDO.getEnabledLanguages()) {
            enabledLanguages.add(languageRepository.findByCode(l.getCode()));
        }

        meta.setEnabledLanguages(enabledLanguages);
        meta.setId(metaDO.getId());
        meta.setKeywords(metaDO.getKeywords());
        meta.setLinkableByOtherUsers(metaDO.getLinkableByOtherUsers());
        meta.setLinkedForUnauthorizedUsers(metaDO.getLinkedForUnauthorizedUsers());
        meta.setModifiedDatetime(metaDO.getModifiedDatetime());
        //e.setPermissionSets(m.getPermissionSets)
        //e.setPermissionSetsForNew(m.getPermissionSetExForNew)
        //e.setPermissionSetsForNewDocuments(m.getPermissionSetsForNewDocuments)
        meta.setProperties(metaDO.getProperties());
        meta.setPublicationEndDatetime(metaDO.getPublicationEndDatetime());
        meta.setPublicationStartDatetime(metaDO.getPublicationStartDatetime());
        meta.setPublicationStatusInt(metaDO.getPublicationStatus().asInt());
        meta.setPublisherId(metaDO.getPublisherId());
        meta.setRestrictedOneMorePrivilegedThanRestrictedTwo(metaDO.getRestrictedOneMorePrivilegedThanRestrictedTwo());
        //e.setRoleIdToPermissionSetIdMap()
        meta.setSearchDisabled(metaDO.getSearchDisabled());
        meta.setTarget(metaDO.getTarget());

        return meta;
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public void setDocumentMapper(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    public DocRepository getDocRepository() {
        return docRepository;
    }

    public void setDocRepository(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    public VersionRepository getVersionRepository() {
        return versionRepository;
    }

    public void setVersionRepository(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }
}