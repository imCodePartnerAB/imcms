package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.domain.service.DocumentRolesService;
import com.imcode.imcms.persistence.entity.DocumentRole;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.DocumentRolesRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultDocumentRolesService implements DocumentRolesService {

    private final DocumentRolesRepository documentRolesRepository;
    private final MetaRepository metaRepository;

    public DefaultDocumentRolesService(DocumentRolesRepository documentRolesRepository,
                                       MetaRepository metaRepository) {

        this.documentRolesRepository = documentRolesRepository;
        this.metaRepository = metaRepository;
    }

    @Override
    public DocumentRoles getDocumentRoles(int documentId, UserDomainObject user) {
        final List<DocumentRole> roleList = documentRolesRepository.getDocumentRolesByUserIdAndDocId(
                user.getId(), documentId
        );

        final Meta meta = metaRepository.findOne(documentId);

        return new DocumentRoles(roleList, meta);
    }
}
