package com.imcode.imcms.mapping;

import com.imcode.imcms.DocIdentityCleanerVisitor;
import com.imcode.imcms.dao.*;
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

import java.util.*;

import org.springframework.transaction.annotation.Transactional;

/**
 * Used internally by DocumentMapper. Must NOT be used directly.
 * <p/>
 * Instantiated and initialized using spring framework.
 */
public class DocumentSaver {

    private DocumentMapper documentMapper;

    private MetaDao metaDao;

    private DocVersionDao documentVersionDao;

    private TextDocDao textDocDao;

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
    public void saveText(TextDomainObject text, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        createEnclosingContentLoopIfNecessary(text.getI18nDocRef().getDocRef(), text.getContentLoopRef());

        new DocumentStoringVisitor(Imcms.getServices()).saveTextDocumentText(text, user);

        metaDao.touch(text.getI18nDocRef().getDocRef(), user);
    }


    @Transactional
    public void saveTexts(Collection<TextDomainObject> texts, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        for (TextDomainObject text : texts) {
            saveText(text, user);
        }
    }


    @Transactional
    public void saveMenu(MenuDomainObject menu, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        new DocumentStoringVisitor(Imcms.getServices()).updateTextDocumentMenu(menu, user);

        metaDao.touch(menu.getDocRef(), user);
    }


    /**
     * Saves changed text-document image(s).
     * If an image is enclosed into unsaved content loop then this content loop is also saved.
     *
     * @param images images with the same 'no' for every language.
     * @param user
     * @throws NoPermissionInternalException
     * @throws DocumentSaveException
     * @see com.imcode.imcms.servlet.admin.ChangeImage
     */
    @Transactional
    public void saveImages(Collection<ImageDomainObject> images, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        for (ImageDomainObject image : images) {
            saveImage(image, user);
        }
    }


    @Transactional
    public void saveImage(ImageDomainObject image, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        createEnclosingContentLoopIfNecessary(image.getDocRef(), image.getContentLoopRef());

        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        storingVisitor.saveTextDocumentImage(image, user);

        metaDao.touch(image.getDocRef(), user);
    }


    /**
     * Creates content loop if item references non-saved enclosing content loop.
     */
    @Transactional
    public TextDocLoop createEnclosingContentLoopIfNecessary(DocRef docRef, TextDocLoopItemRef contentLoopRef) {
        if (contentLoopRef == null) {
            return null;
        }

        TextDocLoop loop = textDocDao.getLoop(docRef, contentLoopRef.getLoopNo());
        ContentLoopOps ops = new ContentLoopOps(loop);

        if (loop == null) {
            throw new IllegalStateException(String.format(
                    "Content loop does not exists. Doc identity: %s, content loop no: %s.", docRef, contentLoopRef.getLoopNo()));
        }

        if (ops.findContent(contentLoopRef.getContentNo()).isPresent()) {
            throw new IllegalStateException(String.format(
                    "Content does not exists. Doc identity :%s, content loop no: %s.", docRef, contentLoopRef.getLoopNo()));
        }

        loop = textDocDao.saveLoop(loop);

        return loop;
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
     *
     * @throws DocumentSaveException
     */
    @Transactional
    public DocVersion makeDocumentVersion(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentDomainObject firstDoc = docs.get(0);
        DocMeta meta = firstDoc.getMeta().clone();
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

        return nextVersion;
    }


    @Transactional
    public void updateDocument(DocumentDomainObject doc, Set<I18nMeta> i18nMetas, DocumentDomainObject oldDoc, UserDomainObject user) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        checkDocumentForSave(doc);

        if (user.canEditPermissionsFor(oldDoc)) {
            newUpdateDocumentRolePermissions(doc, user, oldDoc);
            documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(doc, user, oldDoc);
        }

        DocumentSavingVisitor savingVisitor = new DocumentSavingVisitor(oldDoc, documentMapper.getImcmsServices(), user);

        saveMeta(doc.getMeta());

        for (I18nMeta i18nMeta : i18nMetas) {
            metaDao.saveI18nMeta(i18nMeta);
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
     *
     * @throws DocumentSaveException
     */
    @Transactional
    public Integer copyDocument(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentDomainObject firstDoc = docs.get(0);
        DocMeta meta = firstDoc.getMeta().clone();

        checkDocumentForSave(firstDoc);

        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());

        newUpdateDocumentRolePermissions(firstDoc, user, null);

        // Update permissions
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(firstDoc, user, null);

        meta.setId(null);
        Integer copyDocId = saveMeta(meta).getId();
        metaDao.insertPropertyIfNotExists(copyDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, copyDocId.toString());
        for (DocumentDomainObject doc : docs) {
            I18nMeta i18nMeta = I18nMeta.builder(doc.getI18nMeta()).id(null).docId(copyDocId).build();

            metaDao.saveI18nMeta(i18nMeta);
        }

        DocVersion copyDocVersion = documentVersionDao.createVersion(copyDocId, user.getId());
        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(copyDocVersion.getNo());
        }

        // Currently only text doc has i18n content.
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(docCreatingVisitor);
        } else {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) firstDoc;

            docCreatingVisitor.updateTextDocumentContentLoops(textDoc, user);
            docCreatingVisitor.updateTextDocumentMenus(textDoc, user);
            docCreatingVisitor.updateTextDocumentTemplateNames(textDoc, user);
            docCreatingVisitor.updateTextDocumentIncludes(textDoc);

            for (DocumentDomainObject doc : docs) {
                textDoc = (TextDocumentDomainObject) doc;
                docCreatingVisitor.updateTextDocumentTexts(textDoc, user);
                docCreatingVisitor.updateTextDocumentImages(textDoc, user);
            }
        }

        return copyDocId;
    }


    /**
     * Please note that custom (limited) permissions might be changed on save:
     * -If saving user is a super-admin or have full perms on a doc, then all custom perms settings are merely inherited.
     * -Otherwise custom (lim1 and lim2) perms are replaced with permissions set for new document.
     * <p/>
     * If user is a super-admin or has full permissions on a new document then
     *
     * @param doc
     * @param i18nMetas
     * @param directiveses
     * @param user
     * @param <T>
     * @return
     * @throws NoPermissionToAddDocumentToMenuException
     *
     * @throws DocumentSaveException
     */
    @Transactional
    public <T extends DocumentDomainObject> int saveNewDocument(T doc, Set<I18nMeta> i18nMetas, EnumSet<DocumentMapper.SaveOpts> directiveses, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocMeta meta = doc.getMeta();

        checkDocumentForSave(doc);

        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());

        if (!user.isSuperAdminOrHasFullPermissionOn(doc)) {
            meta.getPermissionSets().setRestricted1(meta.getPermissionSetsForNewDocuments().getRestricted1());
            meta.getPermissionSets().setRestricted2(meta.getPermissionSetsForNewDocuments().getRestricted2());
        }

        newUpdateDocumentRolePermissions(doc, user, null);
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(doc, user, null);

        meta.setId(null);
        meta.setDefaultVersionNo(DocVersion.WORKING_VERSION_NO);
        meta.setDocumentType(doc.getDocumentTypeId());
        Integer docId = saveMeta(meta).getId();

        for (I18nMeta i18nMeta : i18nMetas) {
            metaDao.saveI18nMeta(I18nMeta.builder(i18nMeta).id(null).docId(docId).build());
        }

        metaDao.insertPropertyIfNotExists(docId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, docId.toString());

        DocVersion version = documentVersionDao.createVersion(docId, user.getId());
        doc.setVersionNo(version.getNo());

        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        doc.accept(docCreatingVisitor);

        // refactor
        if (doc instanceof TextDocumentDomainObject && directiveses.contains(DocumentMapper.SaveOpts.CopyI18nMetaTextsIntoTextFields)) {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) doc;
            for (I18nMeta i18nMeta : i18nMetas) {
                TextDomainObject text1 = new TextDomainObject(i18nMeta.getHeadline(), TextDomainObject.TEXT_TYPE_PLAIN);
                TextDomainObject text2 = new TextDomainObject(i18nMeta.getMenuText(), TextDomainObject.TEXT_TYPE_PLAIN);

                text1.setNo(1);
                text1.setI18nDocRef(textDoc.getI18nRef());

                text2.setNo(2);
                text2.setI18nDocRef(textDoc.getI18nRef());

                docCreatingVisitor.saveTextDocumentText(text1, user);
                docCreatingVisitor.saveTextDocumentText(text2, user);
            }
        }

        return docId;
    }


    /**
     * @return saved document meta.
     */
    private DocMeta saveMeta(DocMeta meta) {
        meta.setPublicationStatusInt(meta.getPublicationStatus().asInt());

        if (meta.getId() == null) {
            meta.setActivate(1);
        }

        metaDao.saveMeta(meta);

        return meta;
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
        Map<Integer, Integer> roleIdToPermissionSetIdMap = document.getMeta().getRoleIdToPermissionSetIdMap();

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