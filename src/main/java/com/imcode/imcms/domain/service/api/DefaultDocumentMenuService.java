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

import java.util.*;
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
    public boolean hasUserAccessToDoc(DocumentDTO documentDTO, UserDomainObject user){
        return user.hasUserAccessToDoc(documentDTO) ||
                (user.isDefaultUser() && documentDTO.isLinkableForUnauthorizedUsers());
    }

    @Override
    public Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId) {
	    return metaRepository.getOne(documentId).getDisabledLanguageShowMode();
    }

    @Override
    public MenuItemDTO getMenuItemDTO(MenuItem menuItem) {
	    final Integer docId = menuItem.getDocumentId();
        final Version currentVersion = versionService.getLatestVersion(docId);

	    final Meta metaDocument = metaRepository.getOne(docId);
        final List<CommonContent> commonContentList = commonContentService
                .getOrCreateCommonContents(docId, currentVersion.getNo());

        return getMenuItemDTO(menuItem, metaToDocumentDTO.apply(metaDocument, commonContentList));
    }

    @Override
    public MenuItemDTO getMenuItemDTO(MenuItem menuItem, DocumentDTO doc){
        final List<CommonContent> enabledCommonContents = doc.getCommonContents().stream()
                .filter(CommonContent::isEnabled)
                .collect(Collectors.toList());

        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setDocumentId(doc.getId());
        menuItemDTO.setType(doc.getType());
        menuItemDTO.setTitle(getHeadlineInCorrectLanguage(enabledCommonContents, doc.getDisabledLanguageShowMode()));
        menuItemDTO.setMenuText(getMenuTextInCorrectLanguage(enabledCommonContents, doc.getDisabledLanguageShowMode()));
        menuItemDTO.setLink("/" + doc.getName());
        menuItemDTO.setTarget(doc.getTarget());
        menuItemDTO.setDocumentStatus(doc.getDocumentStatus());
        menuItemDTO.setCreatedDate(Utility.convertDateToLocalDateTime(doc.getCreated().getFormattedDate()));
        menuItemDTO.setPublishedDate(Utility.convertDateToLocalDateTime(doc.getPublished().getFormattedDate()));
        menuItemDTO.setModifiedDate(Utility.convertDateToLocalDateTime(doc.getModified().getFormattedDate()));
        menuItemDTO.setCreatedBy(doc.getCreated().getBy());
        menuItemDTO.setPublishedBy(doc.getPublished().getBy());
        menuItemDTO.setModifiedBy(doc.getModified().getBy());
        menuItemDTO.setHasNewerVersion(doc.getCurrentVersion().getId() == WORKING_VERSION_NO);
        menuItemDTO.setIsDefaultLanguageAliasEnabled(doc.isDefaultLanguageAliasEnabled());
        menuItemDTO.setIsShownTitle(getIsShownTitle(doc));
        menuItemDTO.setSortOrder(menuItem.getSortOrder());
        menuItemDTO.setLinkableByOtherUsers(doc.isLinkableByOtherUsers());
        menuItemDTO.setLinkableForUnauthorizedUsers(doc.isLinkableForUnauthorizedUsers());

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

    @Override
    public boolean isPublicMenuItem(DocumentDTO documentDTO) {
        return (isDocumentApproved(documentDTO)
                && isNotArchivedYet(documentDTO)
                && isNotUnPublishedYet(documentDTO)
                && isAlreadyPublished(documentDTO));
    }

    private boolean isDocumentApproved(Meta meta) {
        return Meta.PublicationStatus.APPROVED.equals(meta.getPublicationStatus());
    }

    private boolean isDocumentApproved(DocumentDTO documentDTO) {
        return Meta.PublicationStatus.APPROVED.equals(documentDTO.getPublicationStatus());
    }

    private boolean isNotArchivedYet(Meta meta) {
        return Utility.isDateInFutureOrNull.test(meta.getArchivedDatetime());
    }

    private boolean isNotArchivedYet(DocumentDTO documentDTO) {
        return Utility.isDateInFutureOrNull.test(documentDTO.getArchived().getFormattedDate());
    }

    private boolean isNotUnPublishedYet(Meta meta) {
        return Utility.isDateInFutureOrNull.test(meta.getPublicationEndDatetime());
    }

    private boolean isNotUnPublishedYet(DocumentDTO documentDTO) {
        return Utility.isDateInFutureOrNull.test(documentDTO.getPublicationEnd().getFormattedDate());
    }

    private boolean isAlreadyPublished(Meta meta) {
        return Utility.isDateInPast.test(meta.getPublicationStartDatetime());
    }

    private boolean isAlreadyPublished(DocumentDTO documentDTO) {
        return Utility.isDateInPast.test(documentDTO.getPublished().getFormattedDate());
    }
}
