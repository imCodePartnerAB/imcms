package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.component.ImageCacheManager;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import com.imcode.imcms.util.Value;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;

/**
 * Service for work with common document entities.
 * Every specified document type has it's own corresponding service.
 */
@Service
@Transactional
class DefaultDocumentService implements DocumentService<DocumentDTO> {

    private final Logger LOGGER = LogManager.getLogger(DefaultDocumentService.class);

    private final TextDocumentTemplateRepository textDocumentTemplateRepository;
    private final MetaRepository metaRepository;
    private final BiFunction<Meta, List<CommonContent>, DocumentDTO> documentMapping;
    private final CommonContentService commonContentService;
    private final VersionService versionService;
    private final TextService textService;
    private final ImageService imageService;
    private final LoopService loopService;
    private final DocumentsCache documentsCache;
    private final List<VersionedContentService> versionedContentServices;
    private final Function<DocumentDTO, Meta> documentSaver;
    private final MenuService menuService;
    private final Function<Menu, MenuDTO> menuToMenuDTO;
    private final Function<ImageJPA, ImageDTO> imageJPAToImageDTO;
    private final ImageCacheManager imageCacheManager;

    private DeleterByDocumentId[] docContentServices = {};

    DefaultDocumentService(TextDocumentTemplateRepository textDocumentTemplateRepository, MetaRepository metaRepository,
                           BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                           Function<DocumentDTO, Meta> documentDtoToMeta,
                           CommonContentService commonContentService,
                           VersionService versionService,
                           TextService textService,
                           ImageService imageService,
                           LoopService loopService,
                           DocumentsCache documentsCache,
                           @Qualifier("versionedContentServices") List<VersionedContentService> versionedContentServices,
                           MenuService menuService, Function<Menu, MenuDTO> menuToMenuDTO,
                           Function<ImageJPA, ImageDTO> imageJPAToImageDTO,
                           ImageCacheManager imageCacheManager) {

        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
        this.metaRepository = metaRepository;
        this.documentMapping = metaToDocumentDTO;
        this.commonContentService = commonContentService;
        this.versionService = versionService;
        this.textService = textService;
        this.imageService = imageService;
        this.loopService = loopService;
        this.documentsCache = documentsCache;
        this.versionedContentServices = versionedContentServices;
        this.menuService = menuService;
        this.menuToMenuDTO = menuToMenuDTO;
        this.imageJPAToImageDTO = imageJPAToImageDTO;
        this.imageCacheManager = imageCacheManager;
        this.documentSaver = ((Function<Meta, Meta>) metaRepository::save).compose(documentDtoToMeta);
    }

    @PostConstruct
    public void init() {
        docContentServices = new DeleterByDocumentId[]{
                textService,
                imageService,
                loopService,
                commonContentService,
        };
    }

    @Override
    public long countDocuments() {
        return metaRepository.count();
    }

    @Override
    public DocumentDTO createFromParent(Integer parentDocId) {
        DocumentDTO parentClone = get(parentDocId).clone();
        parentClone.setLatestVersion(parentClone.getCurrentVersion());
        return parentClone;
    }

    @Override
    public DocumentDTO get(int docId) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        return get(docId, workingVersion.getNo());
    }

    @Override
    public DocumentDTO get(int docId, int versionNo) {
        final List<CommonContent> commonContents = commonContentService.getOrCreateCommonContents(docId, versionNo);
        return documentMapping.apply(metaRepository.getOne(docId), commonContents);
    }

    @Override
    public DocumentDTO save(DocumentDTO saveMe) {
	    final boolean isNew = (saveMe.getId() == null);

	    saveMe.getCommonContents().forEach(commonContent -> {
		    final String alias = commonContent.getAlias();
		    if (!Objects.equals(alias, "")) {
			    CommonContent dbCommonContent = commonContentService.getByAlias(alias);
			    if (dbCommonContent != null && !dbCommonContent.getId().equals(commonContent.getId())) {
				    commonContent.setAlias("");
			    }
		    }
	    });

        final Map<Integer, Meta.Permission> roleIdToPermission = saveMe.getRoleIdToPermission();
        roleIdToPermission.remove(Roles.USER.getId());
        saveMe.setRoleIdToPermission(roleIdToPermission);

        saveMe.setDocumentWasteBasket(metaRepository.findWasteBasket(saveMe.getId()));

	    final Meta meta = documentSaver.apply(saveMe);
        final Integer docId = meta.getId();

        if (isNew) {
            saveMe.setId(docId);
            versionService.create(docId);
            saveMe.getCommonContents().forEach(commonContentDTO -> commonContentDTO.setDocId(docId));
        }

        //Prevent unnecessary saving and version updating
        if(isNew || (!saveMe.getCommonContents().equals(commonContentService.getOrCreateCommonContents(docId, WORKING_VERSION_INDEX)))){
            commonContentService.save(docId, saveMe.getCommonContents());
        }

	    return get(docId);
    }

    private AuditDTO auditData(Date date, UserDomainObject currentUser) {
        final AuditDTO auditDTO = new AuditDTO();
        auditDTO.setDateTime(date);
        if (currentUser != null) {
            auditDTO.setId(currentUser.getId());
            auditDTO.setBy(currentUser.getLogin());
        }
        return auditDTO;
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
	    if (!versionService.hasNewerVersion(docId)) return false;

	    final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
	    final Version newVersion = versionService.create(docId, userId);

	    versionedContentServices.forEach(vcs -> vcs.createVersionedContent(workingVersion, newVersion));

	    final Meta publishMe = metaRepository.getOne(docId);

	    final Collection<String> aliases = get(docId).getCommonContents().stream().map(CommonContent::getAlias).collect(Collectors.toList());

	    documentsCache.invalidateDoc(docId, aliases);
	    imageCacheManager.removePublicImagesFromCacheByKey(String.valueOf(docId));

	    if (Meta.PublicationStatus.NEW.equals(publishMe.getPublicationStatus())) {
		    publishMe.setPublicationStatus(Meta.PublicationStatus.APPROVED);
	    }

	    publishMe.setDefaultVersionNo(newVersion.getNo());

	    final Date publicationStartDatetime = publishMe.getPublicationStartDatetime();

	    if (publicationStartDatetime == null) {
		    publishMe.setPublisherId(userId);
		    publishMe.setPublicationStartDatetime(new Date());
	    }
        metaRepository.save(publishMe);

        return true;
    }

    @Override
    public void setAsWorkingVersion(int docId, int versionNo){
        final Version version = versionService.findByDocIdAndNo(docId, versionNo);

        commonContentService.setAsWorkingVersion(version);
        loopService.setAsWorkingVersion(version);
        textService.setAsWorkingVersion(version);
        imageService.setAsWorkingVersion(version);
        menuService.setAsWorkingVersion(version);

        versionService.updateWorkingVersion(docId);
    }

    @Override
    public SolrInputDocument index(int docId) {
        final DocumentDTO doc = get(docId);

        final Integer currentVersionDocNo = versionService.getCurrentVersion(docId).getNo();

        SolrInputDocument indexDoc = new SolrInputDocument();

        BiConsumer<String, Object> addFieldIfNotNull = (name, value) -> {
            if (value != null) indexDoc.addField(name, value);
        };

        indexDoc.addField(DocumentIndex.FIELD__ID, docId);
        indexDoc.addField(DocumentIndex.FIELD__TIMESTAMP, new Date());
        indexDoc.addField(DocumentIndex.FIELD__META_ID, docId);
        indexDoc.addField(DocumentIndex.FIELD__VERSION_NO, currentVersionDocNo);
        indexDoc.addField(DocumentIndex.FIELD__SEARCH_ENABLED, !doc.isSearchDisabled());
	    indexDoc.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, doc.getDisabledLanguageShowMode().name());
	    indexDoc.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, doc.isDefaultLanguageAliasEnabled());

        for (CommonContent commonContent : doc.getCommonContents()) {
            String headline = StringUtils.defaultString(commonContent.getHeadline());
	        String menuText = commonContent.getMenuText();
	        String alias = StringUtils.defaultString(commonContent.getAlias());

	        final String langCode = commonContent.getLanguage().getCode();
	        indexDoc.addField(DocumentIndex.FIELD__LANGUAGE_CODE, langCode);
	        indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE + "_" + langCode, headline);
	        //copied for search ignore case sensitivity
	        indexDoc.addField(DocumentIndex.FIELD_META_HEADLINE + "_" + langCode, headline.toLowerCase());

	        indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE_KEYWORD + "_" + langCode, headline);
	        indexDoc.addField(DocumentIndex.FIELD__META_TEXT + "_" + langCode, menuText);

	        indexDoc.addField(DocumentIndex.FIELD__META_ALIAS + "_" + langCode, alias);
	        //copied for search ignore case sensitivity
	        indexDoc.addField(DocumentIndex.FIELD_META_ALIAS + "_" + langCode, alias.toLowerCase());

	        indexDoc.addField(DocumentIndex.FIELD__META_ALIAS_KEYWORD + "_" + langCode, alias);

	        if (commonContent.isEnabled()) {
		        indexDoc.addField(DocumentIndex.FIELD__ENABLED_LANGUAGE_CODE, langCode);
	        }
        }

        indexDoc.addField(DocumentIndex.FIELD__DOC_TYPE_ID, doc.getType().ordinal());
        indexDoc.addField(DocumentIndex.FIELD__CREATOR_ID, doc.getCreated().getId());
        indexDoc.addField(DocumentIndex.FIELD__CREATOR_NAME, doc.getCreated().getBy());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__PUBLISHER_ID, doc.getPublished().getId());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__MODIFIER_NAME, doc.getModified().getBy());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__PUBLISHER_NAME, doc.getPublished().getBy());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__CREATED_DATETIME, doc.getCreated().getFormattedDate());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__MODIFIED_DATETIME, doc.getCurrentVersion().getFormattedDate());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__ACTIVATED_DATETIME, doc.getPublished().getFormattedDate());
        addFieldIfNotNull.accept(DocumentIndex.FIELD__PUBLICATION_START_DATETIME,
                doc.getPublished().getFormattedDate());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__PUBLICATION_END_DATETIME,
                doc.getPublicationEnd().getFormattedDate());

        addFieldIfNotNull.accept(DocumentIndex.FIELD__ARCHIVED_DATETIME, doc.getArchived().getFormattedDate());

        indexDoc.addField(DocumentIndex.FIELD__STATUS, doc.getPublicationStatus().ordinal());

        doc.getCategories().forEach(category -> {
            indexDoc.addField(DocumentIndex.FIELD__CATEGORY, category.getName());
            indexDoc.addField(DocumentIndex.FIELD__CATEGORY_ID, category.getId());

            Value.with(category.getType(), categoryType -> {
                indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName());
                indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE_ID, categoryType.getId());

            });
        });

        doc.getKeywords().
                forEach(documentKeyword -> indexDoc.addField(DocumentIndex.FIELD__KEYWORD, documentKeyword));

        doc.getProperties()
                .forEach((key, value) -> indexDoc.addField(DocumentIndex.FIELD__PROPERTY_PREFIX + key, value));

        for (Integer roleId : doc.getRoleIdToPermission().keySet()) {
            indexDoc.addField(DocumentIndex.FIELD__ROLE_ID, roleId);
        }

        indexDoc.addField(DocumentIndex.FIELD__VISIBLE, doc.isVisible());
        indexDoc.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, doc.isLinkableForUnauthorizedUsers());
        indexDoc.addField(DocumentIndex.FIELD__LINKABLE_OTHER, doc.isLinkableByOtherUsers());
        indexDoc.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, doc.getDocumentWasteBasket() != null);

        return indexDoc;
    }


	@Override
	public SolrInputDocument updateDocumentVersion(int docId) {
		final DocumentDTO doc = get(docId);

		final Integer currentVersionDocNo = versionService.getCurrentVersion(docId).getNo();
		final SolrInputDocument indexDoc = new SolrInputDocument();

		final BiConsumer<String, Object> addFieldIfNotNull = (name, value) -> {
			if (value != null) indexDoc.addField(name, Map.of("set",value));
		};

		indexDoc.addField(DocumentIndex.FIELD__ID, docId);

		addFieldIfNotNull.accept(DocumentIndex.FIELD__TIMESTAMP, new Date());

		doc.getCommonContents().forEach(commonContent -> {
			addFieldIfNotNull.accept(DocumentIndex.FIELD__LANGUAGE_CODE, commonContent.getLanguage().getCode());
		});

		addFieldIfNotNull.accept(DocumentIndex.FIELD__META_ID, docId);
		addFieldIfNotNull.accept(DocumentIndex.FIELD__VERSION_NO, currentVersionDocNo);
		addFieldIfNotNull.accept(DocumentIndex.FIELD__SEARCH_ENABLED, !doc.isSearchDisabled());
		addFieldIfNotNull.accept(DocumentIndex.FIELD__VISIBLE, doc.isVisible());


		return indexDoc;
	}

    @Override
    public DocumentDTO copy(int docId) {
        final DocumentDTO documentDTO = get(docId);

        documentDTO.getCommonContents()
                .forEach(commonContentDTO ->
                        commonContentDTO.setHeadline(commonContentDTO.getHeadline() + " (Copy/Kopia)"));

        final DocumentDTO clonedDocumentDTO = documentDTO.clone();

        final DocumentDTO savedDoc = save(clonedDocumentDTO);

        savingContentsFromMainToCopiedDocument(docId, savedDoc);

        return savedDoc;
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        deleteDocumentContent(docIdToDelete);

	    metaRepository.deleteById(docIdToDelete);
    }

    @Override
    public List<DocumentDTO> getDocumentsByTemplateName(String templateName) {
        List<Integer> documentIds = textDocumentTemplateRepository.findDocIdByTemplateName(templateName);

        return documentIds.stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public int countDocumentsByTemplateName(String templateName) {
        return textDocumentTemplateRepository.countDocByTemplateName(templateName);
    }

    @Override
    public String getUniqueAlias(String alias) {
        if (!commonContentService.existsByAlias(alias)) {
            return alias;
        }

        int i = 1;
        while (commonContentService.existsByAlias(alias + "-" + i++)) ;

        return alias + "-" + (i - 1);
    }

    protected void deleteDocumentContent(Integer docIdToDelete) {
        for (DeleterByDocumentId docContentService : docContentServices) {
            docContentService.deleteByDocId(docIdToDelete);
        }
    }


    private void savingContentsFromMainToCopiedDocument(Integer mainCoppingDocId, DocumentDTO copiedDoc) {

        final Integer copiedDocId = copiedDoc.getId();

        savingMenuFromMainToCopied(mainCoppingDocId, copiedDocId);

        savingImagesFromMainToCopied(mainCoppingDocId, copiedDocId);

        savingTextsFromMainToCopied(mainCoppingDocId, copiedDocId);

        savingLoopFromMainToCopied(mainCoppingDocId, copiedDocId);

    }

    private void savingMenuFromMainToCopied(Integer mainCoppingDocId, Integer copiedDocId) {
        menuService.getByDocId(mainCoppingDocId)
                .stream()
                .map(menuToMenuDTO)
                .forEach(menuDTO -> {
                    menuDTO.setDocId(copiedDocId);
                    menuService.saveFrom(menuDTO);
                });
    }

    private void savingImagesFromMainToCopied(Integer mainCoppingDocId, Integer copiedDocId) {
        imageService.getByDocId(mainCoppingDocId)
                .stream()
                .map(imageJPAToImageDTO)
                .forEach(imageDTO -> {
                    imageDTO.setDocId(copiedDocId);
                    imageService.saveImage(imageDTO);
                });
    }

    private void savingTextsFromMainToCopied(Integer mainCoppingDocId, Integer copiedDocId) {
        textService.getByDocId(mainCoppingDocId)
                .stream()
                .map(textJPA -> {
                    try {
                        return textJPA.clone();
                    } catch (CloneNotSupportedException e) {
                        LOGGER.error("CloneNotSupportedException in copping texts from main document id !" + mainCoppingDocId);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(textJPA -> {
                    textJPA.setId(null);
                    textJPA.setVersion(versionService.getLatestVersion(copiedDocId));
                    textService.save(textJPA);
                });
    }

    private void savingLoopFromMainToCopied(Integer mainCoppingDocId, Integer copiedDocId) {
        loopService.getByVersion(versionService.getLatestVersion(mainCoppingDocId))
                .stream()
                .map(LoopDTO::new)
                .forEach(loopDTO -> {
                    loopDTO.setDocId(copiedDocId);
                    loopService.saveLoop(loopDTO);
                });
    }
}
