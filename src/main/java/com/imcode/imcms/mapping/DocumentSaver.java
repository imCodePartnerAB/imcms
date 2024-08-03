package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.controller.exception.NoPermissionInternalException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.PropertyService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.exception.AliasAlreadyExistsInternalException;
import com.imcode.imcms.mapping.exception.DocumentSaveException;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used internally by DocumentMapper.
 */
@Service
@Slf4j
public class DocumentSaver {

    private final DocRepository docRepository;
    private final VersionRepository versionRepository;
    private final VersionService versionService;
    private final LanguageRepository languageRepository;
    private final CommonContentRepository commonContentRepository;
    private final CommonContentService commonContentService;
    private final MetaRepository metaRepository;
    private final TextDocumentContentSaver textDocumentContentSaver;
    private final DocumentContentMapper documentContentMapper;
    private final DocumentVersionMapper versionMapper;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;
    private final DocumentCreatingVisitor documentCreatingVisitor;
    private final DocumentSavingVisitor documentSavingVisitor;
    private DefaultDocumentMapper documentMapper;

    public DocumentSaver(DocRepository docRepository, VersionRepository versionRepository,
                         VersionService versionService, LanguageRepository languageRepository,
                         CommonContentRepository commonContentRepository, CommonContentService commonContentService, MetaRepository metaRepository,
                         TextDocumentContentSaver textDocumentContentSaver, DocumentContentMapper documentContentMapper,
                         DocumentVersionMapper versionMapper, PropertyRepository propertyRepository,
                         PropertyService propertyService, DocumentCreatingVisitor documentCreatingVisitor,
                         DocumentSavingVisitor documentSavingVisitor) {

        this.docRepository = docRepository;
        this.versionRepository = versionRepository;
        this.versionService = versionService;
        this.languageRepository = languageRepository;
        this.commonContentRepository = commonContentRepository;
        this.commonContentService = commonContentService;
        this.metaRepository = metaRepository;
        this.textDocumentContentSaver = textDocumentContentSaver;
        this.documentContentMapper = documentContentMapper;
        this.versionMapper = versionMapper;
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
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
                               Map<Language, DocumentCommonContent> commonContents,
                               UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        Meta jpaMeta = toJpaObject(doc.getMeta());
        jpaMeta.setModifierId(user.getId());

        log.error("updateDocument: meta_id - {}, modifierId - {}", jpaMeta.getId(), jpaMeta.getModifierId());
        metaRepository.saveAndFlush(jpaMeta);

        commonContents.forEach((language, dcc) -> {
            CommonContentJPA ormDcc = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(
                    doc.getId(), doc.getVersionNo(), language.getCode());
            if (ormDcc == null) {
                ormDcc = new CommonContentJPA();
            }

            ormDcc.setHeadline(dcc.getHeadline());
            ormDcc.setMenuText(dcc.getMenuText());
            ormDcc.setEnabled(dcc.getEnabled());
            ormDcc.setVersionNo(doc.getVersionNo());

            if (ormDcc.getId() == null) {
                LanguageJPA ormLanguage = languageRepository.findByCode(language.getCode());

                ormDcc.setDocId(doc.getId());
                ormDcc.setLanguage(ormLanguage);
                log.error("updateDocument: doc language - {}, doc_id - {}", ormLanguage, doc.getId());
                commonContentRepository.save(ormDcc);
            }
        });

        doc.accept(documentSavingVisitor);
        updateModifiedDtIfNotSetExplicitly(doc);
        log.error("updateDocument: doc version_no - {}, doc_id - {}, user_id - {}", doc.getVersionRef().getNo(), doc.getVersionRef().getDocId(), user.getId());
        docRepository.touch(doc.getVersionRef(), user, doc.getModifiedDatetime());
    }

    /**
     * @deprecated use
     */
    @Deprecated
    @Transactional
    public void updateDocument(DocumentDomainObject doc,
                               Map<Language, DocumentCommonContent> commonContents,
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
     *
     * If user is a super-admin or has full permissions on a new document then
     */
    @Transactional
    public <T extends DocumentDomainObject> int saveNewDocument(T doc,
                                                                Map<Language, DocumentCommonContent> dccMap,
                                                                EnumSet<DefaultDocumentMapper.SaveOpts> saveOpts,
                                                                UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        DocumentMeta metaDO = doc.getMeta().clone();
        documentMapper.setCreatedAndModifiedDatetimes(metaDO, new Date());
        metaDO.setId(null);
        metaDO.setDefaultVersionNo(DocumentVersion.WORKING_VERSION_NO);
        metaDO.setDocumentTypeId(doc.getDocumentTypeId());

        Meta jpaMeta = toJpaObject(metaDO);
        jpaMeta.setModifierId(user.getId());
        log.error("saveNewDocument: jpaId - {}, jpaModifierId - {}", jpaMeta.getId(), jpaMeta.getModifierId());
        int newDocId = metaRepository.saveAndFlush(jpaMeta).getId();
        log.error("saveNewDocument: new docId - {}", newDocId);

        dccMap.forEach((language, dcc) -> {
            LanguageJPA jpaLanguage = languageRepository.findByCode(language.getCode());
            commonContentService.getOrCreate(newDocId, DocumentVersion.WORKING_VERSION_NO, jpaLanguage);
        });

	    log.error("saveNewDocument: find by doc id META - {}", metaRepository.getOne(newDocId).getId());

        Version version = versionService.create(newDocId, user.getId());
        doc.setVersionNo(version.getNo());
        doc.setId(newDocId);

        log.error("saveNewDocument: prepare to doc accept - create visitor");
        doc.accept(documentCreatingVisitor);
        log.error("saveNewDocument: doc accept completed!");

        if (doc instanceof TextDocumentDomainObject
                && saveOpts.contains(DefaultDocumentMapper.SaveOpts.CopyDocCommonContentIntoTextFields))
        {
            Map<Language, TextDomainObject> texts1 = new HashMap<>();
            Map<Language, TextDomainObject> texts2 = new HashMap<>();

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

        log.error("saveNewDocument: process return docId - {}", newDocId);
        return newDocId;
    }

    /**
     * Various non security checks.
     */
    private void checkDocumentForSave(DocumentDomainObject document)
            throws NoPermissionInternalException, DocumentSaveException {
        checkIfAliasAlreadyExist(document);
    }

    private void checkIfAliasAlreadyExist(DocumentDomainObject document) throws AliasAlreadyExistsInternalException {
	    String alias = document.getAlias();

        if(StringUtils.isBlank(alias)) return;

        List<CommonContent> existingContents = commonContentService.getByAlias(alias);
        if(!existingContents.isEmpty() && existingContents.get(0).getDocId() != document.getId()){
            throw new AliasAlreadyExistsInternalException(
                    String.format("Alias %s is already in use by document %d.", alias, existingContents.get(0).getDocId()));
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
	    meta.setDefaultLanguageAliasEnabled(metaDO.getDefaultLanguageAliasEnabled());
	    meta.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.valueOf(
			    metaDO.getDisabledLanguageShowMode().name()
	    ));
        meta.setDocumentType(DocumentType.values()[metaDO.getDocumentTypeId()]);
        meta.setId(metaDO.getId());
        meta.setKeywords(metaDO.getKeywords());
        meta.setLinkableByOtherUsers(metaDO.getLinkableByOtherUsers());
        meta.setLinkedForUnauthorizedUsers(metaDO.getLinkedForUnauthorizedUsers());
        meta.setCacheForUnauthorizedUsers(metaDO.isCacheForUnauthorizedUsers());
        meta.setCacheForAuthorizedUsers(metaDO.isCacheForAuthorizedUsers());
        meta.setVisible(metaDO.getVisible());
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
        meta.setSearchDisabled(metaDO.isSearchDisabled());
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
