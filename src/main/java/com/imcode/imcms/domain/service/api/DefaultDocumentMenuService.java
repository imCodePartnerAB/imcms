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
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultDocumentMenuService implements DocumentMenuService {

    private final MetaRepository metaRepository;
    private final VersionService versionService;
    private final CommonContentService commonContentService;
    private final TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO;

    DefaultDocumentMenuService(MetaRepository metaRepository,
                               VersionService versionService,
                               CommonContentService commonContentService,
                               TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO) {
        this.metaRepository = metaRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.metaToDocumentDTO = metaToDocumentDTO;
    }

    // it's not adopted to all access rules that could exist
    @Override
    public boolean hasUserAccessToDoc(int docId, UserDomainObject user) {
        final Meta meta = Optional.ofNullable(metaRepository.findOne(docId))
                .orElseThrow(() -> new DocumentNotExistException(docId));

        if (meta.getLinkedForUnauthorizedUsers()) {
            return true;
        }

        final Map<Integer, Permission> docPermissions = meta.getRoleIdToPermission();
        boolean hasAccess;
        if (docPermissions.isEmpty()) {
            hasAccess = user.isSuperAdmin();
        } else {
            hasAccess = user.getRoleIds()
                    .stream()
                    .map(docPermissions::get)
                    .filter(Objects::nonNull)
                    .anyMatch(permission -> permission.isAtLeastAsPrivilegedAs(Permission.VIEW));

        }

        return hasAccess;
    }

    @Override
    public Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId) {
        return metaRepository.findOne(documentId).getDisabledLanguageShowMode();
    }

    @Override
    public MenuItemDTO getMenuItemDTO(MenuItem menuItem, Language language) {
        final Integer docId = menuItem.getDocumentId();
        final Meta metaDocument = metaRepository.findOne(docId);

        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);

        final List<CommonContent> commonContentList = commonContentService
                .getOrCreateCommonContents(docId, workingVersion.getNo());

        final DocumentDTO documentDTO = metaToDocumentDTO.apply(metaDocument, workingVersion, commonContentList);

        final String documentDTOAlias = documentDTO.getAlias();

        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setDocumentId(docId);
        menuItemDTO.setType(documentDTO.getType());
        menuItemDTO.setTitle(getHeadlineInCorrectLanguage(documentDTO));
        menuItemDTO.setLink("/" + (StringUtils.isBlank(documentDTOAlias) ? docId : documentDTOAlias));
        menuItemDTO.setTarget(documentDTO.getTarget());
        menuItemDTO.setDocumentStatus(documentDTO.getDocumentStatus());
        menuItemDTO.setCreatedDate(documentDTO.getCreated().getFormattedDate());
        menuItemDTO.setPublishedDate(documentDTO.getPublished().getFormattedDate());
        menuItemDTO.setModifiedDate(documentDTO.getModified().getFormattedDate());
        menuItemDTO.setCreatedBy(documentDTO.getCreated().getBy());
        menuItemDTO.setPublishedBy(documentDTO.getPublished().getBy());
        menuItemDTO.setModifiedBy(documentDTO.getModified().getBy());
        menuItemDTO.setHasNewerVersion(versionService.hasNewerVersion(docId));
        menuItemDTO.setIsShownTitle(getIsShownTitle(documentDTO));
        menuItemDTO.setSortOrder(menuItem.getSortOrder());

        return menuItemDTO;
    }

    // TODO: Move this logic to some service if it will be used somewhere else
    private String getHeadlineInCorrectLanguage(Document document) {
        final List<CommonContent> enabledCommonContents = document.getCommonContents().stream()
                .filter(CommonContent::isEnabled)
                .collect(Collectors.toList());

        if (enabledCommonContents.size() == 0) {
            return "";
        }

        final Language currentLanguage = Imcms.getLanguage();

        final Optional<String> headlineInCurrentLanguage = getEnabledHeadLine(enabledCommonContents, currentLanguage);

        if (headlineInCurrentLanguage.isPresent()) {
            return headlineInCurrentLanguage.get();
        }

        if (!isShownInDefaultLanguage(document)) {
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
        final Meta meta = metaRepository.findOne(docId);

        if (meta == null) throw new DocumentNotExistException(docId);

        return (isDocumentApproved(meta)
                && isNotArchivedYet(meta)
                && isNotUnPublishedYet(meta)
                && isAlreadyPublished(meta)
                && isDefaultUserPermittedForView(meta)
        );
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

    private boolean isDefaultUserPermittedForView(Meta meta) {
        final Permission userPermission = meta.getRoleIdToPermission().get(Roles.USER.getId());
        return (userPermission == null) || userPermission.isAtLeastAsPrivilegedAs(Permission.VIEW);
    }
}
