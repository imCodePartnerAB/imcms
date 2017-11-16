package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.PermissionDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final MetaRepository metaRepository;
    private final Function<Meta, DocumentDTO> documentMapping;
    private final CommonContentService commonContentService;
    private final Function<DocumentDTO, Meta> metaSaver;

    public DocumentService(MetaRepository metaRepository,
                           Function<Meta, DocumentDTO> metaToDocumentDTO,
                           Function<DocumentDTO, Meta> documentDtoToMeta,
                           CommonContentService commonContentService) {

        this.metaRepository = metaRepository;
        this.documentMapping = metaToDocumentDTO;
        this.commonContentService = commonContentService;
        this.metaSaver = documentDtoToMeta.andThen(metaRepository::save);
    }

    public DocumentDTO get(int docId) {
        return documentMapping.apply(metaRepository.findOne(docId));
    }

    public void save(DocumentDTO saveMe) {
        commonContentService.save(saveMe.getCommonContents());
        metaSaver.apply(saveMe);
    }

    List<DocumentDTO> getAllDocuments() {
        return metaRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Meta::getId))
                .map(documentMapping)
                .collect(Collectors.toList());
    }

    boolean hasUserAccessToDoc(int docId, UserDomainObject user) {
        final Meta meta = Optional.ofNullable(metaRepository.findOne(docId))
                .orElseThrow(() -> new DocumentNotExistException(docId));

        if (meta.getLinkedForUnauthorizedUsers()) {
            return true;
        }

        final Map<Integer, Meta.Permission> docPermissions = meta.getRoleIdToPermissionSetIdMap();

        return Arrays.stream(user.getRoleIds())
                .map(RoleId::getRoleId)
                .map(docPermissions::get)
                .filter(Objects::nonNull)
                .map(PermissionDTO::fromPermission)
                .anyMatch(documentPermissionSetTypeDomainObject
                        -> documentPermissionSetTypeDomainObject.isAtLeastAsPrivilegedAs(PermissionDTO.VIEW));
    }

}
