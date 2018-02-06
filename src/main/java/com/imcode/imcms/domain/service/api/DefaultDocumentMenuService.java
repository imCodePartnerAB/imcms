package com.imcode.imcms.domain.service.api;

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
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static imcode.server.document.DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS;

@Service
public class DefaultDocumentMenuService implements DocumentMenuService {

    private final MetaRepository metaRepository;
    private final VersionService versionService;
    private final CommonContentService commonContentService;

    DefaultDocumentMenuService(MetaRepository metaRepository,
                               VersionService versionService,
                               CommonContentService commonContentService) {
        this.metaRepository = metaRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
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
    public String getDocumentTitle(int documentId, Language language) {
        final Version latestVersion = versionService.getLatestVersion(documentId);

        final CommonContent existingCommonContent = commonContentService
                .getOrCreate(documentId, latestVersion.getNo(), language);

        return existingCommonContent.getHeadline();
    }

    @Override
    public String getDocumentTarget(int documentId) {
        return metaRepository.findTarget(documentId);
    }

    @Override
    public String getDocumentLink(int documentId) {
        final String alias = metaRepository.findOne(documentId)
                .getProperties()
                .get(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);

        return "/" + (alias == null ? documentId : alias);
    }

    @Override
    public Meta.DocumentType getDocumentType(int documentId) {
        return metaRepository.findType(documentId);
    }

    @Override
    public Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId) {
        return metaRepository.findOne(documentId).getDisabledLanguageShowMode();
    }
}
