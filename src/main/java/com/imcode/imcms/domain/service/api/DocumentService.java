package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.dto.PermissionDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.TextDocumentTemplateService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.Value;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static imcode.server.document.DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS;

@Service
public class DocumentService {

    private final MetaRepository metaRepository;
    private final TernaryFunction<Meta, Version, List<CommonContentDTO>, DocumentDTO> documentMapping;
    private final CommonContentService commonContentService;
    private final VersionService versionService;
    private final LanguageService languageService;
    private final TextDocumentTemplateService textDocumentTemplateService;
    private final Function<DocumentDTO, Meta> metaSaver;

    DocumentService(MetaRepository metaRepository,
                    TernaryFunction<Meta, Version, List<CommonContentDTO>, DocumentDTO> metaToDocumentDTO,
                    Function<DocumentDTO, Meta> documentDtoToMeta,
                    CommonContentService commonContentService,
                    VersionService versionService,
                    LanguageService languageService,
                    TextDocumentTemplateService textDocumentTemplateService) {

        this.metaRepository = metaRepository;
        this.documentMapping = metaToDocumentDTO;
        this.commonContentService = commonContentService;
        this.versionService = versionService;
        this.languageService = languageService;
        this.textDocumentTemplateService = textDocumentTemplateService;
        this.metaSaver = documentDtoToMeta.andThen(metaRepository::save);
    }

    public DocumentDTO get(Integer docId) {
        return (docId == null) ? buildNewDocument() : getDocument(docId);
    }

    private DocumentDTO buildNewDocument() {
        final List<CommonContentDTO> commonContents = commonContentService.createCommonContents();

        return Value.with(DocumentDTO.createNew(), documentDTO -> documentDTO.setCommonContents(commonContents));
    }

    private DocumentDTO getDocument(int docId) {
        final Version latestVersion = versionService.getLatestVersion(docId);
        final List<CommonContentDTO> commonContents = commonContentService.getOrCreateCommonContents(
                docId, latestVersion.getNo()
        );
        return documentMapping.apply(metaRepository.findOne(docId), latestVersion, commonContents);
    }

    public void save(DocumentDTO saveMe) {
        commonContentService.save(saveMe.getCommonContents());
        Optional.ofNullable(saveMe.getTemplate()).ifPresent(textDocumentTemplateService::save);
        metaSaver.apply(saveMe);
    }

    List<DocumentDTO> getAllDocuments() {
        return metaRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Meta::getId))
                .map(meta -> {
                    final Integer docId = meta.getId();
                    final Version latestVersion = versionService.getLatestVersion(docId);
                    final List<CommonContentDTO> commonContents = commonContentService.getOrCreateCommonContents(
                            docId, latestVersion.getNo()
                    );
                    return documentMapping.apply(meta, latestVersion, commonContents);
                })
                .collect(Collectors.toList());
    }

    boolean hasUserAccessToDoc(int docId, UserDomainObject user) {
        final Meta meta = Optional.ofNullable(metaRepository.findOne(docId))
                .orElseThrow(() -> new DocumentNotExistException(docId));

        if (meta.getLinkedForUnauthorizedUsers()) {
            return true;
        }

        final Map<Integer, Meta.Permission> docPermissions = meta.getRoleIdToPermission();

        return Arrays.stream(user.getRoleIds())
                .map(RoleId::getRoleId)
                .map(docPermissions::get)
                .filter(Objects::nonNull)
                .map(PermissionDTO::fromPermission)
                .anyMatch(documentPermissionSetTypeDomainObject
                        -> documentPermissionSetTypeDomainObject.isAtLeastAsPrivilegedAs(PermissionDTO.VIEW));
    }

    public String getDocumentTitle(int documentId) {
        final Version latestVersion = versionService.getLatestVersion(documentId);

        // note: for current user language, may be wong!
        final String code = LanguageMapper.convert639_2to639_1(Imcms.getUser().getLanguageIso639_2());
        final LanguageDTO languageDTO = languageService.findByCode(code);

        // fixme: what if such content is disabled?
        final CommonContentDTO commonContent = commonContentService.getOrCreate(
                documentId, latestVersion.getNo(), languageDTO
        );

        return commonContent.getHeadline();
    }

    public String getDocumentTarget(int documentId) {
        return metaRepository.findTarget(documentId);
    }

    public String getDocumentLink(int documentId) {
        final String alias = metaRepository.findOne(documentId)
                .getProperties()
                .get(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);

        return "/" + (alias == null ? documentId : alias);
    }
}
