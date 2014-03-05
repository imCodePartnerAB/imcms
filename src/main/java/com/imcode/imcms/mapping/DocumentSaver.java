package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.doc.*;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Text;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.TextRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.TextType;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
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
    private DocVersionRepository docVersionRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private TextRepository textRepository;

    @Inject
    private LoopRepository loopRepository;

    @Inject
    private CommonContentRepository commonContentRepository;

    @Inject
    private MetaRepository metaRepository;
    
    @Inject
    private TextDocumentContentSaver textDocumentContentSaver;

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
    public void saveText(TextDocTextContainer textContainer, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveText(textContainer, user);
        docRepository.touch(textContainer.getDocRef(), user);
    }

    @Transactional
    public void saveTexts(Collection<TextDocTextContainer> texts, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        for (TextDocTextContainer textContainer : texts) {
            saveText(textContainer, user);
        }
    }


    @Transactional
    public void saveMenu(TextDocMenuContainer conteiner, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveMenu(conteiner, user);
        docRepository.touch(conteiner.getDocVersionRef(), user);
    }


    /**
     * Saves changed text-document image(s).
     * If an image is enclosed into unsaved content loop then this content loop is also saved.
     *
     * @param images
     * @param user
     * @throws NoPermissionInternalException
     * @throws DocumentSaveException
     */
    @Transactional
    public void saveImages(Collection<TextDocImageContainer> images, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        for (TextDocImageContainer imageContainer : images) {
            saveImage(imageContainer, user);
        }
    }


    @Transactional
    public void saveImage(TextDocImageContainer imageContainer, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        textDocumentContentSaver.saveImage(imageContainer, user);
        docRepository.touch(imageContainer.getDocRef(), user);
    }


    @Transactional
    public void changeDocumentDefaultVersion(int docId, int newDefaultDocVersionNo, UserDomainObject publisher) {
        Version currentDefaultVersion = docVersionRepository.findDefault(docId);

        if (currentDefaultVersion.getNo() != newDefaultDocVersionNo) {
            docVersionRepository.setDefault(docId, newDefaultDocVersionNo, publisher.getId());

            docRepository.touch(DocVersionRef.of(docId, newDefaultDocVersionNo), publisher);
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
        DocumentVersion nextDocVersion = versionMapper.create(meta.getId(), user.getId());
        DocumentSavingVisitor docSavingVisitor = new DocumentSavingVisitor(null, documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(nextDocVersion.getNo());

            docSavingVisitor.saveCommonContent(doc, user);
        }

        // Currently only text doc has i18n content.
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(docSavingVisitor);
        } else {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) firstDoc;
            //fixme - assign doc id, version
            textDocumentContentSaver.saveLoops(textDoc, user);
            textDocumentContentSaver.saveMenus(textDoc, user);
            textDocumentContentSaver.saveTemplateNames(textDoc, user);
            textDocumentContentSaver.saveIncludes(textDoc, user);

            for (DocumentDomainObject doc : docs) {
                textDoc = (TextDocumentDomainObject) doc;
                textDocumentContentSaver.saveTexts(textDoc, user);
                textDocumentContentSaver.saveImages(textDoc, user);
            }
        }

        return nextDocVersion;
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
        docRepository.touch(doc.getRef(), user, doc.getModifiedDatetime());
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

        Version copyVersion = docVersionRepository.create(newDocId, user.getId());
        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(copyVersion.getNo());
            docCreatingVisitor.saveCommonContent(doc, user);
        }

        // Currently only text docs contain non-common i18n content
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(docCreatingVisitor);
        } else {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) firstDoc;

            // loops, menus, template-names and includes are shared
            textDocumentContentSaver.saveLoops(textDoc, user);
            textDocumentContentSaver.saveMenus(textDoc, user);
            textDocumentContentSaver.saveTemplateNames(textDoc, user);
            textDocumentContentSaver.saveIncludes(textDoc, user);

            // i18n content
            for (DocumentDomainObject doc : docs) {
                textDoc = (TextDocumentDomainObject) doc;
                textDocumentContentSaver.saveTexts(textDoc, user);
                textDocumentContentSaver.saveImages(textDoc, user);
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

        DocumentMeta documentMeta = doc.getMeta().clone();
        documentMapper.setCreatedAndModifiedDatetimes(documentMeta, new Date());
        documentMeta.setId(null);
        documentMeta.setDefaultVersionNo(DocumentVersion.WORKING_VERSION_NO);
        documentMeta.setDocumentType(doc.getDocumentTypeId());

        if (!user.isSuperAdminOrHasFullPermissionOn(doc)) {
            documentMeta.getPermissionSets().setRestricted1(documentMeta.getPermissionSetsForNewDocument().getRestricted1());
            documentMeta.getPermissionSets().setRestricted2(documentMeta.getPermissionSetsForNewDocument().getRestricted2());
        }

        Meta jpaMeta = toJpaObject(documentMeta);

        newUpdateDocumentRolePermissions(jpaMeta, doc, user, null);
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(jpaMeta, doc, user, null);

        int newDocId = metaRepository.saveAndFlush(jpaMeta).getId();

        for (Map.Entry<DocumentLanguage, DocumentCommonContent> e : dccMap.entrySet()) {
            DocumentCommonContent dcc = e.getValue();
            CommonContent ormDcc = new CommonContent();
            Language ormLanguage = languageRepository.findByCode(e.getKey().getCode());

            ormDcc.setDocId(newDocId);
            ormDcc.setHeadline(dcc.getHeadline());
            ormDcc.setMenuImageURL(dcc.getMenuImageURL());
            ormDcc.setMenuText(dcc.getMenuText());
            ormDcc.setLanguage(ormLanguage);

            commonContentRepository.save(ormDcc);
        }

        docRepository.insertPropertyIfNotExists(newDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, String.valueOf(newDocId));

        Version version = docVersionRepository.create(newDocId, user.getId());
        doc.setVersionNo(version.getNo());
        doc.setId(newDocId);

        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        doc.accept(docCreatingVisitor);

        // todo: refactor
        if (doc instanceof TextDocumentDomainObject && saveOpts.contains(DocumentMapper.SaveOpts.CopyDocCommonContentIntoTextFields)) {
            Version ormVersion = docVersionRepository.findByDocIdAndNo(doc.getId(), doc.getVersionNo());

            for (Map.Entry<DocumentLanguage, DocumentCommonContent> e : dccMap.entrySet()) {
                DocumentCommonContent dcc = e.getValue();
                Language ormLanguage = languageRepository.findByCode(e.getKey().getCode());

                Text text1 = new Text(ormVersion, ormLanguage, TextType.PLAIN_TEXT, 1, null, dcc.getHeadline());
                Text text2 = new Text(ormVersion, ormLanguage, TextType.PLAIN_TEXT, 1, null, dcc.getMenuText());

                textRepository.save(text1);
                textRepository.save(text2);
            }
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

    // fixme: copy permission
    private Meta toJpaObject(DocumentMeta metaDO) {
        Meta jpaMeta = new Meta();

        jpaMeta.setArchivedDatetime(metaDO.getArchivedDatetime());
        jpaMeta.setCategoryIds(metaDO.getCategoryIds());
        jpaMeta.setCreatedDatetime(metaDO.getCreatedDatetime());
        jpaMeta.setCreatorId(metaDO.getCreatorId());
        jpaMeta.setDefaultVersionNo(metaDO.getDefaultVersionNo());
        jpaMeta.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.valueOf(metaDO.getDisabledLanguageShowMode().name()));
        jpaMeta.setDocumentType(metaDO.getDocumentType());

        Set<Language> enabledLanguages = new HashSet<>();

        for (DocumentLanguage l : metaDO.getEnabledLanguages()) {
            enabledLanguages.add(languageRepository.findByCode(l.getCode()));
        }

        jpaMeta.setEnabledLanguages(enabledLanguages);
        jpaMeta.setId(metaDO.getId());
        jpaMeta.setKeywords(metaDO.getKeywords());
        jpaMeta.setLinkableByOtherUsers(metaDO.getLinkableByOtherUsers());
        jpaMeta.setLinkedForUnauthorizedUsers(metaDO.getLinkedForUnauthorizedUsers());
        jpaMeta.setModifiedDatetime(metaDO.getModifiedDatetime());
        //e.setPermissionSets(m.getPermissionSets)
        //e.setPermissionSetsForNew(m.getPermissionSetExForNew)
        //e.setPermissionSetsForNewDocuments(m.getPermissionSetsForNewDocuments)
        jpaMeta.setProperties(metaDO.getProperties());
        jpaMeta.setPublicationEndDatetime(metaDO.getPublicationEndDatetime());
        jpaMeta.setPublicationStartDatetime(metaDO.getPublicationStartDatetime());
        jpaMeta.setPublicationStatusInt(metaDO.getPublicationStatus().asInt());
        jpaMeta.setPublisherId(metaDO.getPublisherId());
        jpaMeta.setRestrictedOneMorePrivilegedThanRestrictedTwo(metaDO.getRestrictedOneMorePrivilegedThanRestrictedTwo());
        //e.setRoleIdToPermissionSetIdMap()
        jpaMeta.setSearchDisabled(metaDO.getSearchDisabled());
        jpaMeta.setTarget(metaDO.getTarget());

        return jpaMeta;
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

    public DocVersionRepository getDocVersionRepository() {
        return docVersionRepository;
    }

    public void setDocVersionRepository(DocVersionRepository docVersionRepository) {
        this.docVersionRepository = docVersionRepository;
    }
}