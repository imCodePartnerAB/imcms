package com.imcode.imcms.mapping;

import com.imcode.imcms.DocIdentityCleanerVisitor;
import com.imcode.imcms.api.*;
import com.imcode.imcms.dao.DocLanguageDao;
import com.imcode.imcms.dao.DocVersionDao;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.dao.TextDocDao;
import com.imcode.imcms.mapping.orm.*;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.*;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Used internally by DocumentMapper. Must NOT be used directly.
 * <p/>
 * Instantiated and initialized using spring framework.
 */
@Service
public class DocumentSaver {

    private DocumentMapper documentMapper;

    @Inject
    private MetaDao metaDao;

    @Inject
    private DocVersionDao documentVersionDao;

    @Inject
    private TextDocDao textDocDao;

    @Inject
    private DocLanguageDao docLanguageDao;

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
     * @see com.imcode.imcms.servlet.admin.SaveText
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     */
    @Transactional
    public void saveText(TextDocItemRef<TextDomainObject> textRef, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        createEnclosingContentLoopIfMissing(textRef.getDocRef(), textRef.getItem().getContentLoopRef());

        new DocumentStoringVisitor(Imcms.getServices()).saveTextDocumentText(textRef, user);

        metaDao.touch(textRef.getDocRef(), user);
    }


    @Transactional
    public void saveTexts(Collection<TextDocItemRef<TextDomainObject>> textRefs, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        for (TextDocItemRef<TextDomainObject> textRef : textRefs) {
            saveText(textRef, user);
        }
    }


    @Transactional
    public void saveMenu(DocRef docRef, MenuDomainObject menu, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        new DocumentStoringVisitor(Imcms.getServices()).updateTextDocumentMenu(docRef, menu, user);

        metaDao.touch(docRef, user);
    }


    /**
     * Saves changed text-document image(s).
     * If an image is enclosed into unsaved content loop then this content loop is also saved.
     *
     * @param imageRefs
     * @param user
     * @throws NoPermissionInternalException
     * @throws DocumentSaveException
     * @see com.imcode.imcms.servlet.admin.ChangeImage
     */
    @Transactional
    public void saveImages(Collection<TextDocItemRef<ImageDomainObject>> imageRefs, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        for (TextDocItemRef<ImageDomainObject> imageRef : imageRefs) {
            saveImage(imageRef, user);
        }
    }


    @Transactional
    public void saveImage(TextDocItemRef<ImageDomainObject> imageRef, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        ImageDomainObject image = imageRef.getItem();

        createEnclosingContentLoopIfMissing(imageRef.getDocRef(), image.getContentLoopRef());

        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        storingVisitor.saveTextDocumentImage(image, user);

        metaDao.touch(imageRef.getDocRef(), user);
    }


    /**
     * Creates content loop if item references non-saved enclosing content loop.
     */
    @Transactional
    public void createEnclosingContentLoopIfMissing(DocRef docRef, ContentLoopRef contentLoopRef) {
        if (contentLoopRef == null) {
            return;
        }

        TextDocLoop ormLoop = textDocDao.getLoop(docRef, contentLoopRef.getLoopNo());

        if (ormLoop == null) {
            ormLoop = new TextDocLoop();
            ormLoop.setNo(contentLoopRef.getLoopNo());
            ormLoop.setItems(new LinkedList<>(Arrays.asList(new TextDocLoopItem(contentLoopRef.getContentNo()))));
            textDocDao.saveLoop(ormLoop);
        } else {
            ContentLoop apiLoop = OrmToApi.toApi(ormLoop);
            int contentNo = contentLoopRef.getContentNo();
            if (!apiLoop.findContentByNo(contentNo).isPresent()) {
                ormLoop.getItems().add(new TextDocLoopItem(contentNo));
                textDocDao.saveLoop(ormLoop);
            }
        }
    }


    @Transactional
    public void changeDocumentDefaultVersion(int docId, int newDefaultDocVersionNo, UserDomainObject publisher) {
        DocVersion currentDefaultVersion = documentVersionDao.getDefaultVersion(docId);

        if (currentDefaultVersion.getNo() != newDefaultDocVersionNo) {
            DocVersion version = documentVersionDao.getVersion(docId, newDefaultDocVersionNo);
            if (version == null) {
                throw new IllegalStateException(
                        String.format("Doc %d default version can not be changed. Version no %d does not exists.",
                                docId, newDefaultDocVersionNo));
            }

            documentVersionDao.changeDefaultVersion(version, publisher);

            metaDao.touch(DocRef.of(docId, newDefaultDocVersionNo), publisher);
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
        Meta meta = firstDoc.getMeta().clone();
        DocVersion nextVersion = documentVersionDao.createVersion(meta.getId(), user.getId());
        DocumentSavingVisitor docSavingVisitor = new DocumentSavingVisitor(null, documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.accept(new DocIdentityCleanerVisitor());
            doc.setMeta(meta);
            doc.setVersionNo(nextVersion.getNo());

            docSavingVisitor.updateDocumentI18nMeta(doc, user);
        }

        // Currently only text doc has i18n content.
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(docSavingVisitor);
        } else {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) firstDoc;

            docSavingVisitor.updateTextDocumentContentLoops(textDoc, user);
            docSavingVisitor.updateTextDocumentMenus(textDoc, user);
            docSavingVisitor.updateTextDocumentTemplateNames(textDoc, user);
            docSavingVisitor.updateTextDocumentIncludes(textDoc);

            for (DocumentDomainObject doc : docs) {
                textDoc = (TextDocumentDomainObject) doc;
                docSavingVisitor.updateTextDocumentTexts(textDoc, user);
                docSavingVisitor.updateTextDocumentImages(textDoc, user);
            }
        }

        return OrmToApi.toApi(nextVersion);
    }

    //fixme: meta permissions
    @Transactional
    public void updateDocument(DocumentDomainObject doc, Map<DocumentLanguage, DocumentAppearance> appearances, DocumentDomainObject oldDoc,
                               UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        //DocMeta ormMeta = new DocMeta

        if (user.canEditPermissionsFor(oldDoc)) {
            newUpdateDocumentRolePermissions(doc, user, oldDoc);
            documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(doc, user, oldDoc);
        }

        DocumentSavingVisitor savingVisitor = new DocumentSavingVisitor(oldDoc, documentMapper.getImcmsServices(), user);

        saveMeta(doc.getMeta());

        for (Map.Entry<DocumentLanguage, DocumentAppearance> e : appearances.entrySet()) {
            DocumentLanguage language = e.getKey();
            DocumentAppearance appearance = e.getValue();
            DocAppearance ormAppearance = metaDao.getDocAppearance(DocRef.of(doc.getId(), doc.getVersionNo(), language));
            if (ormAppearance == null) {
                ormAppearance = new DocAppearance();
            }

            ormAppearance.setHeadline(appearance.getHeadline());
            ormAppearance.setMenuImageURL(appearance.getMenuImageURL());
            ormAppearance.setMenuText(appearance.getMenuText());

            if (ormAppearance.getId() == null) {
                DocLanguage ormLanguage = docLanguageDao.getByCode(language.getCode());

                ormAppearance.setDocId(doc.getId());
                ormAppearance.setLanguage(ormLanguage);
                metaDao.saveAppearance(ormAppearance);
            }
        }

        doc.accept(savingVisitor);
        updateModifiedDtIfNotSetExplicitly(doc);
        metaDao.touch(doc.getRef(), user, doc.getModifiedDatetime());
    }


    /**
     * @param docs
     * @param user
     * @return
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    //fixme: meta persmissions
    //fixme: implement
    @Transactional
    public Integer copyDocument(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        return 0;

//        DocumentDomainObject firstDoc = docs.get(0);
//        Meta meta = firstDoc.getMeta().clone();
//
//        checkDocumentForSave(firstDoc);
//
//        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());
//
//        newUpdateDocumentRolePermissions(firstDoc, user, null);
//
//        // Update permissions
//        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(firstDoc, user, null);
//
//        meta.setId(null);
//        Integer copyDocId = saveMeta(meta);
//        metaDao.insertPropertyIfNotExists(copyDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, copyDocId.toString());
//        for (DocumentDomainObject doc : docs) {
//            I18nMeta i18nMeta = I18nMeta.builder(doc.getAppearance()).id(null).docId(copyDocId).build();
//
//            metaDao.saveAppearance(i18nMeta);
//        }
//
//        DocVersion copyDocVersion = documentVersionDao.createVersion(copyDocId, user.getId());
//        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);
//
//        for (DocumentDomainObject doc : docs) {
//            doc.setMeta(meta);
//            doc.setVersionNo(copyDocVersion.getNo());
//        }
//
//        // Currently only text doc has i18n content.
//        if (!(firstDoc instanceof TextDocumentDomainObject)) {
//            firstDoc.accept(docCreatingVisitor);
//        } else {
//            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) firstDoc;
//
//            docCreatingVisitor.updateTextDocumentContentLoops(textDoc, user);
//            docCreatingVisitor.updateTextDocumentMenus(textDoc, user);
//            docCreatingVisitor.updateTextDocumentTemplateNames(textDoc, user);
//            docCreatingVisitor.updateTextDocumentIncludes(textDoc);
//
//            for (DocumentDomainObject doc : docs) {
//                textDoc = (TextDocumentDomainObject) doc;
//                docCreatingVisitor.updateTextDocumentTexts(textDoc, user);
//                docCreatingVisitor.updateTextDocumentImages(textDoc, user);
//            }
//        }
//
//        return copyDocId;
    }


    /**
     * Please note that custom (limited) permissions might be changed on save:
     * -If saving user is a super-admin or have full perms on a doc, then all custom perms settings are merely inherited.
     * -Otherwise custom (lim1 and lim2) perms are replaced with permissions set for new document.
     * <p/>
     * If user is a super-admin or has full permissions on a new document then
     *
     * @param doc
     * @param appearances
     * @param directiveses
     * @param user
     * @param <T>
     * @return
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    @Transactional
    public <T extends DocumentDomainObject> int saveNewDocument(T doc, Map<DocumentLanguage, DocumentAppearance> appearances,
                                                                EnumSet<DocumentMapper.SaveOpts> directiveses, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Meta meta = doc.getMeta();

        checkDocumentForSave(doc);

        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());

        if (!user.isSuperAdminOrHasFullPermissionOn(doc)) {
            meta.getPermissionSets().setRestricted1(meta.getPermissionSetsForNewDocuments().getRestricted1());
            meta.getPermissionSets().setRestricted2(meta.getPermissionSetsForNewDocuments().getRestricted2());
        }

        newUpdateDocumentRolePermissions(doc, user, null);
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(doc, user, null);

        meta.setId(null);
        meta.setDefaultVersionNo(DocumentVersion.WORKING_VERSION_NO);
        meta.setDocumentType(doc.getDocumentTypeId());
        Integer docId = saveMeta(meta);

        for (Map.Entry<DocumentLanguage, DocumentAppearance> e : appearances.entrySet()) {
            DocumentAppearance appearance = e.getValue();
            DocAppearance ormAppearance = new DocAppearance();
            DocLanguage ormLanguage = docLanguageDao.getByCode(e.getKey().getCode());

            ormAppearance.setDocId(docId);
            ormAppearance.setHeadline(appearance.getHeadline());
            ormAppearance.setMenuImageURL(appearance.getMenuImageURL());
            ormAppearance.setMenuText(appearance.getMenuText());
            ormAppearance.setLanguage(ormLanguage);

            metaDao.saveAppearance(ormAppearance);
        }

        metaDao.insertPropertyIfNotExists(docId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, docId.toString());

        DocVersion version = documentVersionDao.createVersion(docId, user.getId());
        doc.setVersionNo(version.getNo());

        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        doc.accept(docCreatingVisitor);

        // refactor
        if (doc instanceof TextDocumentDomainObject && directiveses.contains(DocumentMapper.SaveOpts.CopyDocAppearenceIntoTextFields)) {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) doc;
            DocVersion ormVersion = documentVersionDao.getByDocIdAndNo(doc.getId(), doc.getVersionNo());

            for (Map.Entry<DocumentLanguage, DocumentAppearance> e : appearances.entrySet()) {
                DocumentAppearance appearance = e.getValue();
                DocLanguage ormLanguage = docLanguageDao.getByCode(e.getKey().getCode());


                TextDocText text1 = new TextDocText();
                TextDocText text2 = new TextDocText();

                text1.setNo(1);
                text2.setNo(2);

                text1.setDocLanguage(ormLanguage);
                text2.setDocLanguage(ormLanguage);

                text1.setDocVersion(ormVersion);
                text2.setDocVersion(ormVersion);

                text1.setType(TextDocType.PLAIN_TEXT);
                text2.setType(TextDocType.PLAIN_TEXT);

                text1.setText(appearance.getHeadline());
                text2.setText(appearance.getMenuText());

                textDocDao.saveText(text1);
                textDocDao.saveText(text2);
            }
        }

        return docId;
    }


    /**
     * @return saved document meta.
     */
    private int saveMeta(Meta meta) {
        Set<DocLanguage> enabledLanguages = new HashSet<>();

        for (DocumentLanguage l : meta.getEnabledLanguages()) {
            enabledLanguages.add(docLanguageDao.getByCode(l.getCode()));
        }

        DocMeta ormMeta = new DocMeta();
        ormMeta.setArchivedDatetime(meta.getArchivedDatetime());
        ormMeta.setCategoryIds(meta.getCategoryIds());
        ormMeta.setCreatedDatetime(meta.getCreatedDatetime());
        ormMeta.setCreatorId(meta.getCreatorId());
        ormMeta.setDefaultVersionNo(meta.getDefaultVersionNo());
        ormMeta.setDisabledLanguageShowSetting(DocMeta.DisabledLanguageShowSetting.values()[meta.getDisabledLanguageShowSetting().ordinal()]);
        ormMeta.setDocumentType(meta.getDocumentType());
        ormMeta.setEnabledLanguages(enabledLanguages);
        ormMeta.setId(meta.getId());
        ormMeta.setKeywords(meta.getKeywords());
        ormMeta.setLinkableByOtherUsers(meta.getLinkableByOtherUsers());
        ormMeta.setLinkedForUnauthorizedUsers(meta.getLinkedForUnauthorizedUsers());

        //fixme: move to security section
        //ormMeta.setPermisionSetEx(meta.getPermissionSets());
        //ormMeta.setPermisionSetExForNew();
        //ormMeta.setPermissionSetBitsForNewMap();
        //ormMeta.setPermissionSetBitsMap();

        ormMeta.setProperties(meta.getProperties());
        ormMeta.setPublicationEndDatetime(meta.getPublicationEndDatetime());
        ormMeta.setPublicationStartDatetime(meta.getPublicationStartDatetime());
        ormMeta.setPublicationStatusInt(meta.getPublicationStatus().asInt());
        ormMeta.setPublisherId(meta.getPublisherId());
        ormMeta.setRestrictedOneMorePrivilegedThanRestrictedTwo(meta.getRestrictedOneMorePrivilegedThanRestrictedTwo());

        //fixme: move to security section
        //ormMeta.setRoleIdToPermissionSetIdMap(meta.getRoleIdToDocumentPermissionSetTypeMappings());
        ormMeta.setSearchDisabled(meta.getSearchDisabled());
        ormMeta.setTarget(meta.getTarget());

        metaDao.saveMeta(ormMeta);

        // fixme: flush entityManager to get new id

        return ormMeta.getId();
    }

/*

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

*/


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
    private void newUpdateDocumentRolePermissions(DocumentDomainObject document, UserDomainObject user,
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
        //fixme: fix call
        Map<Integer, Integer> roleIdToPermissionSetIdMap = null;//document.getMeta().getRoleIdToPermissionSetIdMap();

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
            DocProperty property = metaDao.getAliasProperty(alias);
            if (property != null) {
                Integer documentId = document.getId();

                if (!property.getDocId().equals(documentId)) {
                    throw new AliasAlreadyExistsInternalException(
                            String.format("Alias %s is already in use by document %d.", alias, documentId));
                }
            }
        }
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public void setDocumentMapper(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    public MetaDao getMetaDao() {
        return metaDao;
    }

    public void setMetaDao(MetaDao metaDao) {
        this.metaDao = metaDao;
    }

    public DocVersionDao getDocumentVersionDao() {
        return documentVersionDao;
    }

    public void setDocumentVersionDao(DocVersionDao documentVersionDao) {
        this.documentVersionDao = documentVersionDao;
    }

    public TextDocDao getTextDocDao() {
        return textDocDao;
    }

    public void setTextDocDao(TextDocDao textDocDao) {
        this.textDocDao = textDocDao;
    }
}