package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.Loop;
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
        createLoopEntry(textContainer.getDocRef(), textContainer.getLoopEntryRef());

        new DocumentStoringVisitor(Imcms.getServices()).saveTextDocumentText(textContainer, user);

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
    public void saveMenu(TextDocMenuContainer menuWrapper, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        new DocumentStoringVisitor(Imcms.getServices()).updateTextDocumentMenu(menuWrapper, user);

        docRepository.touch(menuWrapper.getDocVersionRef(), user);
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
        createLoopEntry(imageContainer.getDocRef(), imageContainer.getLoopEntryRef());

        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        storingVisitor.saveTextDocumentImage(imageContainer, user);

        docRepository.touch(imageContainer.getDocRef(), user);
    }

    /**
     * Creates content loop if item references non-saved enclosing content loop.
     */
    @Transactional
    public void createLoopEntry(DocRef docRef, LoopEntryRef loopEntryRef) {
        if (loopEntryRef == null) {
            return;
        }

        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop ormLoop = loopRepository.findByDocVersionAndNo(docVersion, loopEntryRef.getLoopNo());

        if (ormLoop == null) {
            ormLoop = new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop();
            ormLoop.setNo(loopEntryRef.getLoopNo());
            ormLoop.getEntries().add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(loopEntryRef.getEntryNo()));
            loopRepository.save(ormLoop);
        } else {
            Loop apiLoop = EntityConverter.fromEntity(ormLoop);
            int contentNo = loopEntryRef.getEntryNo();
            if (!apiLoop.findEntryIndexByNo(contentNo).isPresent()) {
                ormLoop.getEntries().add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(contentNo));
                loopRepository.save(ormLoop);
            }
        }
    }


    @Transactional
    public void changeDocumentDefaultVersion(int docId, int newDefaultDocVersionNo, UserDomainObject publisher) {
        DocVersion currentDefaultDocVersion = docVersionRepository.findDefault(docId);

        if (currentDefaultDocVersion.getNo() != newDefaultDocVersionNo) {
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
        DocVersion nextDocVersion = docVersionRepository.create(meta.getId(), user.getId());
        DocumentSavingVisitor docSavingVisitor = new DocumentSavingVisitor(null, documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(nextDocVersion.getNo());

            docSavingVisitor.saveDocumentCommonContent(doc, user);
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

        return EntityConverter.fromEntity(nextDocVersion);
    }

    @Transactional
    public void updateDocument(DocumentDomainObject doc, Map<DocumentLanguage, DocumentCommonContent> appearances, DocumentDomainObject oldDoc,
                               UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        Meta ormMeta = EntityConverter.toEntity(doc.getMeta());

        if (user.canEditPermissionsFor(oldDoc)) {
            newUpdateDocumentRolePermissions(ormMeta, doc, user, oldDoc);
            documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(ormMeta, doc, user, oldDoc);
        }

        DocumentSavingVisitor savingVisitor = new DocumentSavingVisitor(oldDoc, documentMapper.getImcmsServices(), user);

        saveMeta(ormMeta, doc.getMeta());

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
        Meta ormMeta = EntityConverter.toEntity(meta);

        newUpdateDocumentRolePermissions(ormMeta, firstDoc, user, null);

        // Update permissions
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(ormMeta, firstDoc, user, null);

        int newDocId = saveMeta(ormMeta, meta).getId();

        docRepository.insertPropertyIfNotExists(newDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, Integer.toString(newDocId));

        DocVersion copyDocVersion = docVersionRepository.create(newDocId, user.getId());
        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(copyDocVersion.getNo());
            docCreatingVisitor.saveDocumentCommonContent(doc, user);
        }

        // Currently only text docs contain non-common i18n content
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(docCreatingVisitor);
        } else {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) firstDoc;

            // loops, menus, template-names and includes are shared
            docCreatingVisitor.updateTextDocumentContentLoops(textDoc, user);
            docCreatingVisitor.updateTextDocumentMenus(textDoc, user);
            docCreatingVisitor.updateTextDocumentTemplateNames(textDoc, user);
            docCreatingVisitor.updateTextDocumentIncludes(textDoc);

            // i18n content
            for (DocumentDomainObject doc : docs) {
                textDoc = (TextDocumentDomainObject) doc;
                docCreatingVisitor.updateTextDocumentTexts(textDoc, user);
                docCreatingVisitor.updateTextDocumentImages(textDoc, user);
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

        Meta ormMeta = EntityConverter.toEntity(documentMeta);

        newUpdateDocumentRolePermissions(ormMeta, doc, user, null);
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(ormMeta, doc, user, null);

        int newDocId = saveMeta(ormMeta, documentMeta).getId();

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

        DocVersion docVersion = docVersionRepository.create(newDocId, user.getId());
        doc.setVersionNo(docVersion.getNo());
        doc.setId(newDocId);

        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        doc.accept(docCreatingVisitor);

        // todo: refactor
        if (doc instanceof TextDocumentDomainObject && saveOpts.contains(DocumentMapper.SaveOpts.CopyDocCommonContentIntoTextFields)) {
            DocVersion ormDocVersion = docVersionRepository.findByDocIdAndNo(doc.getId(), doc.getVersionNo());

            for (Map.Entry<DocumentLanguage, DocumentCommonContent> e : dccMap.entrySet()) {
                DocumentCommonContent dcc = e.getValue();
                Language ormLanguage = languageRepository.findByCode(e.getKey().getCode());

                Text text1 = new Text(ormDocVersion, ormLanguage, TextType.PLAIN_TEXT, 1, null, dcc.getHeadline());
                Text text2 = new Text(ormDocVersion, ormLanguage, TextType.PLAIN_TEXT, 1, null, dcc.getMenuText());

                textRepository.save(text1);
                textRepository.save(text2);
            }
        }

        return newDocId;
    }


    /**
     * @return saved document meta.
     */
    private Meta saveMeta(Meta ormMeta, DocumentMeta documentMeta) {
        Set<Language> enabledLanguages = new HashSet<>();

        for (DocumentLanguage l : documentMeta.getEnabledLanguages()) {
            enabledLanguages.add(languageRepository.findByCode(l.getCode()));
        }

        ormMeta.setEnabledLanguages(enabledLanguages);

        return metaRepository.saveAndFlush(ormMeta);
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
    private void newUpdateDocumentRolePermissions(Meta ormMeta, DocumentDomainObject document, UserDomainObject user,
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

        Map<Integer, Integer> roleIdToPermissionSetIdMap = ormMeta.getRoleIdToPermissionSetIdMap();

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