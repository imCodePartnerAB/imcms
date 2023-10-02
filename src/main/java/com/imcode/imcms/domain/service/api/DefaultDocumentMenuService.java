package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.DocumentVersion.WORKING_VERSION_NO;

@Service
@Transactional
public class DefaultDocumentMenuService implements DocumentMenuService {

    private final MetaRepository metaRepository;
    private final VersionService versionService;
    private final CommonContentService commonContentService;
    private final BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO;

    DefaultDocumentMenuService(MetaRepository metaRepository,
                               VersionService versionService,
                               CommonContentService commonContentService,
                               BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO) {
        this.metaRepository = metaRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.metaToDocumentDTO = metaToDocumentDTO;
    }

    @Override
    public boolean hasUserAccessToDoc(int docId, UserDomainObject user) {
        final Meta meta = metaRepository.findById(docId)
		        .orElseThrow(() -> new DocumentNotExistException(docId));

        return user.hasUserAccessToDoc(meta) ||
                (user.isDefaultUser() && meta.getLinkedForUnauthorizedUsers());
    }

    @Override
    public Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId) {
	    return metaRepository.getOne(documentId).getDisabledLanguageShowMode();
    }

    @Override
    public MenuItemDTO getMenuItemDTO(MenuItem menuItem) {
	    final Integer docId = menuItem.getDocumentId();
	    final Meta metaDocument = metaRepository.getOne(docId);

        final Version currentVersion = versionService.getCurrentVersion(docId);

        final List<CommonContent> commonContentList = commonContentService
                .getOrCreateCommonContents(docId, currentVersion.getNo());

        final DocumentDTO documentDTO = metaToDocumentDTO.apply(metaDocument, commonContentList);

        final List<CommonContent> enabledCommonContents = documentDTO.getCommonContents().stream()
                .filter(CommonContent::isEnabled)
                .collect(Collectors.toList());

        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setDocumentId(docId);
        menuItemDTO.setType(documentDTO.getType());
        menuItemDTO.setTitle(getHeadlineInCorrectLanguage(enabledCommonContents, documentDTO.getDisabledLanguageShowMode()));
        menuItemDTO.setMenuText(getMenuTextInCorrectLanguage(enabledCommonContents, documentDTO.getDisabledLanguageShowMode()));
	    menuItemDTO.setLink("/" + documentDTO.getName());
	    menuItemDTO.setTarget(documentDTO.getTarget());
        menuItemDTO.setDocumentStatus(documentDTO.getDocumentStatus());
        menuItemDTO.setCreatedDate(Utility.convertDateToLocalDateTime(documentDTO.getCreated().getFormattedDate()));
        menuItemDTO.setPublishedDate(Utility.convertDateToLocalDateTime(documentDTO.getPublished().getFormattedDate()));
        menuItemDTO.setModifiedDate(Utility.convertDateToLocalDateTime(documentDTO.getModified().getFormattedDate()));
        menuItemDTO.setCreatedBy(documentDTO.getCreated().getBy());
        menuItemDTO.setPublishedBy(documentDTO.getPublished().getBy());
        menuItemDTO.setModifiedBy(documentDTO.getModified().getBy());
	    menuItemDTO.setHasNewerVersion(documentDTO.getCurrentVersion().getId() == WORKING_VERSION_NO);
	    menuItemDTO.setIsDefaultLanguageAliasEnabled(documentDTO.isDefaultLanguageAliasEnabled());
	    menuItemDTO.setIsShownTitle(getIsShownTitle(documentDTO));
        menuItemDTO.setSortOrder(menuItem.getSortOrder());
        menuItemDTO.setLinkableByOtherUsers(documentDTO.isLinkableByOtherUsers());
        menuItemDTO.setLinkableForUnauthorizedUsers(documentDTO.isLinkableForUnauthorizedUsers());

        return menuItemDTO;
    }

    // TODO: Move this logic to some service if it will be used somewhere else
    private String getHeadlineInCorrectLanguage(List<CommonContent> enabledCommonContents, Meta.DisabledLanguageShowMode disabledLanguageShowMode) {

        if (enabledCommonContents.isEmpty()) {
            return "";
        }

        final Language currentLanguage = Imcms.getLanguage();
        final Optional<String> headlineInCurrentLanguage = getEnabledHeadLine(enabledCommonContents, currentLanguage);
        if (headlineInCurrentLanguage.isPresent()) {
            return headlineInCurrentLanguage.get();
        }

        if (disabledLanguageShowMode == Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE) {
            return "";
        }

        final Language defaultLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage();
        return getEnabledHeadLine(enabledCommonContents, defaultLanguage).orElse("");
    }

    private Optional<String> getEnabledHeadLine(List<CommonContent> enabledCommonContents, Language language) {
        return enabledCommonContents.stream()
                .filter(commonContent -> commonContent.getLanguage().getCode().equals(language.getCode()))
                .map(commonContent -> Optional.ofNullable(commonContent.getHeadline()).orElse(""))
                .findFirst();
    }

    private String getMenuTextInCorrectLanguage(List<CommonContent> enabledCommonContents, Meta.DisabledLanguageShowMode disabledLanguageShowMode) {

        if (enabledCommonContents.isEmpty()) {
            return "";
        }

        final Language currentLanguage = Imcms.getLanguage();
        final Optional<String> menuTextInCurrentLanguage = getEnabledMenuText(enabledCommonContents, currentLanguage);
        if (menuTextInCurrentLanguage.isPresent()) {
            return menuTextInCurrentLanguage.get();
        }

        if (disabledLanguageShowMode == Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE) {
            return "";
        }

        final Language defaultLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage();
        return getEnabledMenuText(enabledCommonContents, defaultLanguage).orElse("");
    }

    private Optional<String> getEnabledMenuText(List<CommonContent> enabledCommonContents, Language language) {
        return enabledCommonContents.stream()
                .filter(commonContent -> commonContent.getLanguage().getCode().equals(language.getCode()))
                .map(commonContent -> Optional.ofNullable(commonContent.getMenuText()).orElse(""))
                .findFirst();
    }

    private boolean isShownInDefaultLanguage(Document document) {
        return document.getDisabledLanguageShowMode() == Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
    }

    private boolean getIsShownTitle(Document document) {
        final Language currentLanguage = Imcms.getLanguage();
        final Language defaultLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage();

        return isLanguageEnabled(document, currentLanguage)
                || (isShownInDefaultLanguage(document) && isLanguageEnabled(document, defaultLanguage));
    }

    private boolean isLanguageEnabled(Document document, Language language) {
        return document.getCommonContents().stream()
                .filter(CommonContent::isEnabled)
                .anyMatch(commonContent -> commonContent.getLanguage().equals(language));
    }

    @Override
    public boolean isPublicMenuItem(int docId) {
	    final Meta meta = metaRepository.findById(docId).orElseThrow(() -> new DocumentNotExistException(docId));

        return (isDocumentApproved(meta)
                && isNotArchivedYet(meta)
                && isNotUnPublishedYet(meta)
                && isAlreadyPublished(meta));
    }

    private boolean isDocumentApproved(Meta meta) {
        return Meta.PublicationStatus.APPROVED.equals(meta.getPublicationStatus());
    }

    private boolean isNotArchivedYet(Meta meta) {
        final Date archivedDatetime = meta.getArchivedDatetime();
        return Utility.isDateInFutureOrNull.test(archivedDatetime);
    }

    private boolean isNotUnPublishedYet(Meta meta) {
        final Date publicationEndDatetime = meta.getPublicationEndDatetime();
        return Utility.isDateInFutureOrNull.test(publicationEndDatetime);
    }

    private boolean isAlreadyPublished(Meta meta) {
        final Date publicationStartDatetime = meta.getPublicationStartDatetime();
        return Utility.isDateInPast.test(publicationStartDatetime);
    }
}
