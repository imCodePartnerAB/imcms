package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Meta;
import com.imcode.imcms.mapping.jpa.doc.MetaRepository;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
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

    public DocumentService(MetaRepository metaRepository,
                           Function<Meta, DocumentDTO> documentMapping) {
        this.metaRepository = metaRepository;
        this.documentMapping = documentMapping;
    }

    public DocumentDTO get(int docId) {
        return documentMapping.apply(metaRepository.findOne(docId));
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

        final Map<Integer, Integer> docPermissions = meta.getRoleIdToPermissionSetIdMap();

        return Arrays.stream(user.getRoleIds())
                .map(RoleId::getRoleId)
                .map(docPermissions::get)
                .filter(Objects::nonNull)
                .map(DocumentPermissionSetTypeDomainObject::fromInt)
                .anyMatch(documentPermissionSetTypeDomainObject
                        -> documentPermissionSetTypeDomainObject.isAtLeastAsPrivilegedAs(DocumentPermissionSetTypeDomainObject.READ));
    }

}
