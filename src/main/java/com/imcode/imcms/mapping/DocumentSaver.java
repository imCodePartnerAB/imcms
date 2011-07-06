package com.imcode.imcms.mapping;

import com.imcode.imcms.DocIdentityCleanerVisitor;
import com.imcode.imcms.dao.ContentLoopDao;
import com.imcode.imcms.dao.DocumentVersionDao;
import com.imcode.imcms.dao.ImageDao;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.dao.MenuDao;
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

import com.imcode.imcms.api.*;

/**
 * Used internally by DocumentMapper. Must NOT be used directly.
 * <p/>
 * Instantiated and initialized using spring framework.
 */
public class DocumentSaver {

    private DocumentMapper documentMapper;

    private MetaDao metaDao;

    private DocumentVersionDao documentVersionDao;

    private ContentLoopDao contentLoopDao;

    private TextDao textDao;

    private ImageDao imageDao;

    private MenuDao menuDao;

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
    public void saveText(TextDocumentDomainObject doc, TextDomainObject text, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        createEnclosingContentLoopIfNecessary(doc, text);

        new DocumentStoringVisitor(Imcms.getServices()).saveTextDocumentText(doc, text, user);

        metaDao.touch(doc, user);
    }


    @Transactional
    public void saveMenu(TextDocumentDomainObject doc, MenuDomainObject menu, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        menu.setDocId(doc.getId());
        menu.setDocVersionNo(doc.getVersion().getNo());

        new DocumentStoringVisitor(Imcms.getServices()).updateTextDocumentMenu(doc, menu, user);

        metaDao.touch(doc, user);
    }


    /**
     * Saves changed text-document image(s).
     * If an image is enclosed into unsaved content loop then this content loop is also saved.
     *
     * @param doc
     * @param images images with the same 'no' for every language.
     * @param user
     * @throws NoPermissionInternalException
     * @throws DocumentSaveException
     * @see com.imcode.imcms.servlet.admin.ChangeImage
     */
    @Transactional
    public void saveImages(TextDocumentDomainObject doc, Collection<ImageDomainObject> images, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        createEnclosingContentLoopIfNecessary(doc, images.iterator().next());

        for (ImageDomainObject image : images) {
            storingVisitor.saveTextDocumentImage(doc, image, user);
        }

        metaDao.touch(doc, user);
    }


    @Transactional
    public void saveImage(TextDocumentDomainObject doc, ImageDomainObject image, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        createEnclosingContentLoopIfNecessary(doc, image);

        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        storingVisitor.saveTextDocumentImage(doc, image, user);

        metaDao.touch(doc, user);
    }


    /**
     * Creates content loop if item references non-saved enclosing content loop.
     *
     * @param doc
     * @param item
     */
    @Transactional
    public ContentLoop createEnclosingContentLoopIfNecessary(TextDocumentDomainObject doc, DocContentLoopItem item) {
        ContentLoop loop = null;
        Integer loopNo = item.getContentLoopNo();

        if (loopNo != null) {
            Integer contentNo = item.getContentNo();

            if (contentNo == null) {
                throw new IllegalStateException(String.format(
                        "Content loop's context no is not set. Doc id: %s, item :%s, content loop no: %s.", doc.getId(), item, loopNo));
            }

            loop = doc.getContentLoop(loopNo);

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Content loop does not exists. Doc id: %s, item :%s, content loop no: %s.", doc.getId(), item, loopNo));
            }

            if (!loop.contentExists(contentNo)) {
                throw new IllegalStateException(String.format(
                        "Content does not exists. Doc id: %s, item :%s, content loop no: %s.", doc.getId(), item, loopNo));
            }

            loop = contentLoopDao.saveLoop(loop);
        }

        return loop;
    }


    @Transactional
    public void changeDocumentDefaultVersion(Integer docId, Integer docVersionNo, UserDomainObject publisher) {
        DocumentVersion currentDefaultVersion = documentVersionDao.getDefaultVersion(docId);

        if (!currentDefaultVersion.getNo().equals(docVersionNo)) {
            DocumentVersion version = documentVersionDao.getVersion(docId, docVersionNo);
            if (version == null) {
                throw new IllegalStateException(
                        String.format("Can not change doc %d version. Version no %d does not exists.",
                                docId, docVersionNo));
            }

            documentVersionDao.changeDefaultVersion(docId, version, publisher);
            metaDao.touch(docId, docVersionNo, publisher.getId());
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
    public DocumentVersion makeDocumentVersion(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentDomainObject firstDoc = docs.get(0);
        Meta meta = firstDoc.getMeta().clone();
        DocumentVersion nextVersion = documentVersionDao.createVersion(meta.getId(), user.getId());
        DocumentSavingVisitor docSavingVisitor = new DocumentSavingVisitor(null, documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.accept(new DocIdentityCleanerVisitor());
            doc.setMeta(meta);
            doc.setVersion(nextVersion);

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
    public void updateDocument(DocumentDomainObject doc, List<I18nMeta> i18nMetas, DocumentDomainObject oldDoc, UserDomainObject user) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        checkDocumentForSave(doc);
        //document.loadAllLazilyLoaded();

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
        metaDao.touch(doc, doc.getModifiedDatetime(), user);
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
        Meta meta = firstDoc.getMeta().clone();

        checkDocumentForSave(firstDoc);

        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());

        newUpdateDocumentRolePermissions(firstDoc, user, null);

        // Update permissions
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(firstDoc, user, null);

        meta.setId(null);
        Integer copyDocId = saveMeta(meta).getId();
        metaDao.insertPropertyIfNotExists(copyDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, copyDocId.toString());
        for (DocumentDomainObject doc : docs) {
            I18nMeta i18nMeta = doc.getI18nMeta();

            i18nMeta.setId(null);
            i18nMeta.setDocId(copyDocId);

            metaDao.saveI18nMeta(i18nMeta);
        }

        DocumentVersion copyDocVersion = documentVersionDao.createVersion(copyDocId, user.getId());
        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersion(copyDocVersion);
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


    @Transactional
    public <T extends DocumentDomainObject> Integer saveNewDocument(T doc, List<I18nMeta> i18nMetas, EnumSet<DocumentMapper.SaveDirectives> directiveses, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Meta meta = doc.getMeta();

        checkDocumentForSave(doc);

        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());

        boolean inheritRestrictedPermissions = !user.isSuperAdminOrHasFullPermissionOn(doc);

        if (inheritRestrictedPermissions) {
            meta.getPermissionSets().setRestricted1(meta.getPermissionSetsForNewDocuments().getRestricted1());
            meta.getPermissionSets().setRestricted2(meta.getPermissionSetsForNewDocuments().getRestricted2());
        }

        newUpdateDocumentRolePermissions(doc, user, null);
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(doc, user, null);

        meta.setId(null);
        meta.setDefaultVersionNo(DocumentVersion.WORKING_VERSION_NO);
        meta.setDocumentType(doc.getDocumentTypeId());
        Integer docId = saveMeta(meta).getId();

        for (I18nMeta i18nMeta : i18nMetas) {
            i18nMeta.setId(null);
            i18nMeta.setDocId(docId);

            metaDao.saveI18nMeta(i18nMeta);
        }

        metaDao.insertPropertyIfNotExists(docId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, docId.toString());

        DocumentVersion version = documentVersionDao.createVersion(docId, user.getId());
        doc.setVersion(version);

        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        doc.accept(docCreatingVisitor);

        // refactor
        if (doc instanceof TextDocumentDomainObject && directiveses.contains(DocumentMapper.SaveDirectives.CopyI18nMetaTextsIntoTextFields)) {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) doc;
            for (I18nMeta i18nMeta : i18nMetas) {
                TextDomainObject text1 = new TextDomainObject(i18nMeta.getHeadline(), TextDomainObject.TEXT_TYPE_PLAIN);
                TextDomainObject text2 = new TextDomainObject(i18nMeta.getMenuText(), TextDomainObject.TEXT_TYPE_PLAIN);

                text1.setNo(1);
                text2.setNo(2);
                text1.setLanguage(i18nMeta.getLanguage());
                text2.setLanguage(i18nMeta.getLanguage());

                docCreatingVisitor.saveTextDocumentText(textDoc, text1, user);
                docCreatingVisitor.saveTextDocumentText(textDoc, text2, user);
            }
        }

        return docId;
    }


    /**
     * @return saved document meta.
     */
    private Meta saveMeta(Meta meta) {
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
            DocumentProperty property = metaDao.getAliasProperty(alias);
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

    public ContentLoopDao getContentLoopDao() {
        return contentLoopDao;
    }

    public void setContentLoopDao(ContentLoopDao contentLoopDao) {
        this.contentLoopDao = contentLoopDao;
    }

    public DocumentVersionDao getDocumentVersionDao() {
        return documentVersionDao;
    }

    public void setDocumentVersionDao(DocumentVersionDao documentVersionDao) {
        this.documentVersionDao = documentVersionDao;
    }

    public com.imcode.imcms.dao.TextDao getTextDao() {
        return textDao;
    }

    public void setTextDao(com.imcode.imcms.dao.TextDao textDao) {
        this.textDao = textDao;
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public MenuDao getMenuDao() {
        return menuDao;
    }

    public void setMenuDao(MenuDao menuDao) {
        this.menuDao = menuDao;
    }
}