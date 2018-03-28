package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultDocumentMenuService implements DocumentMenuService {

    private final MetaRepository metaRepository;
    private final VersionService versionService;
    private final CommonContentService commonContentService;
    private TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO;

    DefaultDocumentMenuService(MetaRepository metaRepository,
                               VersionService versionService,
                               CommonContentService commonContentService,
                               TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO) {
        this.metaRepository = metaRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.metaToDocumentDTO = metaToDocumentDTO;
    }

    @Override
    public boolean hasUserAccessToDoc(int docId, UserDomainObject user) {
        final Meta meta = Optional.ofNullable(metaRepository.findOne(docId))
                .orElseThrow(() -> new DocumentNotExistException(docId));

        if (meta.getLinkedForUnauthorizedUsers()) {
            return true;
        }

        final Map<Integer, Permission> docPermissions = meta.getRoleIdToPermission();

        return Arrays.stream(user.getRoleIds())
                .map(RoleId::getRoleId)
                .map(docPermissions::get)
                .filter(Objects::nonNull)
                .anyMatch(permission -> permission.isAtLeastAsPrivilegedAs(Permission.VIEW));
    }

    @Override
    public Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId) {
        return metaRepository.findOne(documentId).getDisabledLanguageShowMode();
    }

    @Override
    public MenuItemDTO getMenuItemDTO(int docId, Language language) {
        final Meta metaDocument = metaRepository.findOne(docId);

        final Version latestVersion = versionService.getLatestVersion(docId);

        final List<CommonContent> commonContentList = commonContentService
                .getOrCreateCommonContents(docId, latestVersion.getNo());

        final DocumentDTO documentDTO = metaToDocumentDTO.apply(metaDocument, latestVersion, commonContentList);

        final CommonContent commonContent = commonContentService.getOrCreate(docId, latestVersion.getNo(), language);

        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setDocumentId(docId);
        menuItemDTO.setType(documentDTO.getType());
        menuItemDTO.setTitle(commonContent.getHeadline());
        menuItemDTO.setLink("/" + (documentDTO.getAlias() == null ? docId : documentDTO.getAlias()));
        menuItemDTO.setTarget(documentDTO.getTarget());
        menuItemDTO.setDocumentStatus(documentDTO.getDocumentStatus());

        return menuItemDTO;
    }
}
