package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.controller.exception.NoPermissionInternalException;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.container.MenuContainer;
import com.imcode.imcms.mapping.container.TextDocImageContainer;
import com.imcode.imcms.mapping.container.TextDocImagesContainer;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.container.TextDocTextsContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings.Mapping;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used internally by DocumentMapper.
 */
@Service
@SuppressWarnings("unused")
public class DocumentSaver {

    private final DocRepository docRepository;
    private final VersionRepository versionRepository;
    private final VersionService versionService;
    private final LanguageRepository languageRepository;
    private final CommonContentRepository commonContentRepository;
    private final MetaRepository metaRepository;
    private final TextDocumentContentSaver textDocumentContentSaver;
    private final DocumentContentMapper documentContentMapper;
    private final DocumentVersionMapper versionMapper;
    private final PropertyRepository propertyRepository;
    private final DocumentCreatingVisitor documentCreatingVisitor;
    private final DocumentSavingVisitor documentSavingVisitor;
    private DefaultDocumentMapper documentMapper;

    @Inject
    public DocumentSaver(DocRepository docRepository, VersionRepository versionRepository,
                         VersionService versionService, LanguageRepository languageRepository,
                         CommonContentRepository commonContentRepository, MetaRepository metaRepository,
                         TextDocumentContentSaver textDocumentContentSaver, DocumentContentMapper documentContentMapper,
                         DocumentVersionMapper versionMapper, PropertyRepository propertyRepository,
                         DocumentCreatingVisitor documentCreatingVisitor,
                         DocumentSavingVisitor documentSavingVisitor) {

        this.docRepository = docRepository;
        this.versionRepository = versionRepository;
        this.versionService = versionService;
        this.languageRepository = languageRepository;
        this.commonContentRepository = commonContentRepository;
        this.metaRepository = metaRepository;
        this.textDocumentContentSaver = textDocumentContentSaver;
        this.documentContentMapper = documentContentMapper;
        this.versionMapper = versionMapper;
        this.propertyRepository = propertyRepository;
        this.documentCreatingVisitor = documentCreatingVisitor;
        this.documentSavingVisitor = documentSavingVisitor;
    }

    /**
     * Updates doc's last modified date time if it was not set explicitly.
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
    public void saveText(TextDocTextContainer container, UserDomainObject user)
            throws NoPermissionInternalException {
        textDocumentContentSaver.saveText(container, user);
        docRepository.touch(container.getDocVersionRef(), user);
    }

    @Transactional
    public void saveTexts(TextDocTextsContainer container, UserDomainObject user)
            throws NoPermissionInternalException {
        textDocumentContentSaver.saveTexts(container, user);
        docRepository.touch(container.getVersionRef(), user);
    }

    @Transactional
    public void saveImages(TextDocImagesContainer container, UserDomainObject user)
            throws NoPermissionInternalException {
        textDocumentContentSaver.saveImages(container);
        docRepository.touch(container.getVersionRef(), user);
    }

    @Transactional
    public void saveMenu(MenuContainer container, UserDomainObject user)
            throws NoPermissionInternalException {
        textDocumentContentSaver.saveMenu(container);
        docRepository.touch(container.getVersionRef(), user);
    }

    @Transactional
    public void saveImage(TextDocImageContainer container, UserDomainObject user)
            throws NoPermissionInternalException {
        textDocumentContentSaver.saveImage(container);
        docRepository.touch(container.getDocVersionRef(), user);
    }

    @Transactional
    public void changeDocumentDefaultVersion(int docId, int newDefaultVersionNo, UserDomainObject publisher) {
        Version currentDefaultVersion = versionRepository.findDefault(docId);

        if (currentDefaultVersion.getNo() != newDefaultVersionNo) {
            versionRepository.updateDefaultNo(docId, newDefaultVersionNo, publisher.getId());

            docRepository.touch(VersionRef.of(docId, newDefaultVersionNo), publisher);
        }
    }

    public DocumentVersion makeDocumentVersion(List<DocumentDomainObject> docs, UserDomainObject user) {

        DocumentDomainObject firstDoc = docs.get(0);
        DocumentMeta meta = firstDoc.getMeta().clone();
        DocumentVersion nextVersion = versionMapper.create(meta.getId(), user.getId());

        saveContent(user, docs, meta, nextVersion.getNo(), firstDoc);

        return nextVersion;
    }

    @Transactional
    public void updateDocument(DocumentDomainObject doc,
                               Map<DocumentLanguage, DocumentCommonContent> commonContents,
                               UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        Meta jpaMeta = toJpaObject(doc.getMeta());
        jpaMeta.setModifierId(user.getId());

        metaRepository.saveAndFlush(jpaMeta);

        commonContents.forEach((language, dcc) -> {
            CommonContentJPA ormDcc = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(
                    doc.getId(), doc.getVersionNo(), language.getCode());
            if (ormDcc == null) {
                ormDcc = new CommonContentJPA();
            }

            ormDcc.setHeadline(dcc.getHeadline());
            ormDcc.setMenuImageURL(dcc.getMenuImageURL());
            ormDcc.setMenuText(dcc.getMenuText());
            ormDcc.setEnabled(dcc.getEnabled());
            ormDcc.setVersionNo(doc.getVersionNo());

            if (ormDcc.getId() == null) {
                LanguageJPA ormLanguage = languageRepository.findByCode(language.getCode());

                ormDcc.setDocId(doc.getId());
                ormDcc.setLanguage(ormLanguage);
                commonContentRepository.save(ormDcc);
            }
        });

        doc.accept(documentSavingVisitor);
        updateModifiedDtIfNotSetExplicitly(doc);
        docRepository.touch(doc.getVersionRef(), user, doc.getModifiedDatetime());
    }

    /**
     * @deprecated use
     */
    @Deprecated
    @Transactional
    public void updateDocument(DocumentDomainObject doc,
                               Map<DocumentLanguage, DocumentCommonContent> commonContents,
                               DocumentDomainObject oldDoc,
                               UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        updateDocument(doc, commonContents, user);
    }

    @Transactional
    public int saveNewDocsWithCommonMetaAndVersion(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentDomainObject firstDoc = docs.get(0);
        checkDocumentForSave(firstDoc);

        DocumentMeta meta = firstDoc.getMeta().clone();
        documentMapper.setCreatedAndModifiedDatetimes(meta, new Date());

        meta.setId(null);
        Meta jpaMeta = toJpaObject(meta);

        // Update permissions
        int newDocId = metaRepository.saveAndFlush(jpaMeta).getId();
        meta.setId(newDocId);

        docRepository.insertPropertyIfNotExists(
                newDocId,
                DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS,
                Integer.toString(newDocId)
        );
        int versionNo = versionService.create(newDocId, user.getId()).getNo();

        saveContent(user, docs, meta, versionNo, firstDoc);

        return newDocId;
    }

    private void saveContent(UserDomainObject user, List<DocumentDomainObject> docs, DocumentMeta meta, int no,
                             DocumentDomainObject firstDoc) {
        for (DocumentDomainObject doc : docs) {
            doc.setMeta(meta);
            doc.setVersionNo(no);

            documentContentMapper.saveCommonContent(doc);
        }

        // Currently only text docs contain non-common i18n content
        if (!(firstDoc instanceof TextDocumentDomainObject)) {
            firstDoc.accept(documentCreatingVisitor);
        } else {
            textDocumentContentSaver.createCommonContent((TextDocumentDomainObject) firstDoc);

            for (DocumentDomainObject doc : docs) {
                textDocumentContentSaver.createI18nContent((TextDocumentDomainObject) doc, user);
            }
        }
    }

    /**
     * Please note that custom (limited) permissions might be changed on save:
     * -If saving user is a super-admin or have full perms on a doc, then all custom perms settings are merely inherited
     * -Otherwise custom (lim1 and lim2) perms are replaced with permissions set for new document
     * <p/>
     * If user is a super-admin or has full permissions on a new document then
     */
    @Transactional
    public <T extends DocumentDomainObject> int saveNewDocument(T doc,
                                                                Map<DocumentLanguage, DocumentCommonContent> dccMap,
                                                                EnumSet<DefaultDocumentMapper.SaveOpts> saveOpts,
                                                                UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        DocumentMeta metaDO = doc.getMeta().clone();
        documentMapper.setCreatedAndModifiedDatetimes(metaDO, new Date());
        metaDO.setId(null);
        metaDO.setDefaultVersionNo(DocumentVersion.WORKING_VERSION_NO);
        metaDO.setDocumentType(doc.getDocumentTypeId());

        Meta jpaMeta = toJpaObject(metaDO);
        int newDocId = metaRepository.saveAndFlush(jpaMeta).getId();

        dccMap.forEach((language, dcc) -> {
            CommonContentJPA jpaDcc = new CommonContentJPA();
            LanguageJPA jpaLanguage = languageRepository.findByCode(language.getCode());

            jpaDcc.setDocId(newDocId);
            jpaDcc.setHeadline(dcc.getHeadline());
            jpaDcc.setMenuImageURL(dcc.getMenuImageURL());
            jpaDcc.setMenuText(dcc.getMenuText());
            jpaDcc.setLanguage(jpaLanguage);
            jpaDcc.setEnabled(dcc.getEnabled());
            jpaDcc.setVersionNo(DocumentVersion.WORKING_VERSION_NO);

            commonContentRepository.save(jpaDcc);
        });

        docRepository.insertPropertyIfNotExists(
                newDocId,
                DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS,
                String.valueOf(newDocId)
        );

        Version version = versionService.create(newDocId, user.getId());
        doc.setVersionNo(version.getNo());
        doc.setId(newDocId);

        doc.accept(documentCreatingVisitor);

        if (doc instanceof TextDocumentDomainObject
                && saveOpts.contains(DefaultDocumentMapper.SaveOpts.CopyDocCommonContentIntoTextFields))
        {
            Map<DocumentLanguage, TextDomainObject> texts1 = new HashMap<>();
            Map<DocumentLanguage, TextDomainObject> texts2 = new HashMap<>();

            dccMap.forEach((language, dcc) -> {
                texts1.put(language, new TextDomainObject(dcc.getHeadline()));
                texts2.put(language, new TextDomainObject(dcc.getMenuText()));
            });

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
     */
    private void checkDocumentForSave(DocumentDomainObject document)
            throws NoPermissionInternalException, DocumentSaveException {
        documentMapper.getCategoryMapper().checkMaxDocumentCategoriesOfType(document);
        checkIfAliasAlreadyExist(document);
    }

    private void checkIfAliasAlreadyExist(DocumentDomainObject document) throws AliasAlreadyExistsInternalException {
        String alias = document.getAlias();

        if (alias != null) {
            Integer documentId = propertyRepository.findDocIdByAlias(alias);
            if (documentId != null && !documentId.equals(document.getId())) {
                throw new AliasAlreadyExistsInternalException(
                        String.format("Alias %s is already in use by document %d.", alias, documentId));
            }
        }
    }

    // todo: check permission
    private Meta toJpaObject(DocumentMeta metaDO) {
        Meta meta = new Meta();

        meta.setArchivedDatetime(metaDO.getArchivedDatetime());
        meta.setArchiverId(metaDO.getArchiverId());
        meta.setCategories(metaDO.getCategories());
        meta.setCreatedDatetime(metaDO.getCreatedDatetime());
        meta.setCreatorId(metaDO.getCreatorId());
        meta.setDefaultVersionNo(metaDO.getDefaultVersionNo());
        meta.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.valueOf(
                metaDO.getDisabledLanguageShowMode().name()
        ));
        meta.setDocumentType(DocumentType.values()[metaDO.getDocumentType()]);
        meta.setId(metaDO.getId());
        meta.setKeywords(metaDO.getKeywords());
        meta.setLinkableByOtherUsers(metaDO.getLinkableByOtherUsers());
        meta.setLinkedForUnauthorizedUsers(metaDO.getLinkedForUnauthorizedUsers());
        meta.setModifiedDatetime(metaDO.getModifiedDatetime());
        meta.setProperties(metaDO.getProperties());
        meta.setPublicationEndDatetime(metaDO.getPublicationEndDatetime());
        meta.setDepublisherId(metaDO.getDepublisherId());
        meta.setPublicationStartDatetime(metaDO.getPublicationStartDatetime());
        meta.setPublicationStatus(metaDO.getPublicationStatus().asEnum());
        meta.setPublisherId(metaDO.getPublisherId());
        meta.setRoleIdToPermission(
                Stream.of(metaDO.getRoleIdToDocumentPermissionSetTypeMappings().getMappings()).collect(
                        Collectors.toMap(Mapping::getRoleId, Mapping::getDocumentPermissionSetType)
                )
        );
        meta.setSearchDisabled(metaDO.getSearchDisabled());
        meta.setTarget(metaDO.getTarget());

        return meta;
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public void setDocumentMapper(DefaultDocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    public DocRepository getDocRepository() {
        return docRepository;
    }

    public VersionRepository getVersionRepository() {
        return versionRepository;
    }

}
