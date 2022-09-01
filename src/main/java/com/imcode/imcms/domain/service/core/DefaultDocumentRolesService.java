package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.DocumentRolesService;
import com.imcode.imcms.domain.service.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.ExternalUser;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.DocumentRole;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.DocumentRolesRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultDocumentRolesService implements DocumentRolesService {

    private final DocumentRolesRepository documentRolesRepository;
    private final ExternalToLocalRoleLinkService externalToLocalRoleLinkService;
    private final MetaRepository metaRepository;

    public DefaultDocumentRolesService(DocumentRolesRepository documentRolesRepository,
                                       ExternalToLocalRoleLinkService externalToLocalRoleLinkService,
                                       MetaRepository metaRepository) {

        this.documentRolesRepository = documentRolesRepository;
        this.externalToLocalRoleLinkService = externalToLocalRoleLinkService;
        this.metaRepository = metaRepository;
    }

    @Override
    public DocumentRoles getDocumentRoles(int documentId, UserDomainObject user) {
        final Meta meta = metaRepository.getOne(documentId);

        if (user.isImcmsExternal()) {
            final ExternalUser externalUser = (ExternalUser) user;

            final Set<Role> allLinkedLocalRoles = externalToLocalRoleLinkService.toLinkedLocalRoles(
                    externalUser.getExternalRoles()
            );
            final Set<DocumentRole> allDocRoles = documentRolesRepository.findByDocument_Id(documentId);

            final Set<DocumentRole> localDocRolesLinkedToExternal = allDocRoles.stream()
                    .filter(documentRole -> allLinkedLocalRoles.contains(new RoleDTO(documentRole.getRole())))
                    .collect(Collectors.toSet());

            return new DocumentRoles(new ArrayList<>(localDocRolesLinkedToExternal), meta);
        }

        final List<DocumentRole> roleList = documentRolesRepository.getDocumentRolesByUserIdAndDocId(
                user.getId(), documentId
        );

        return new DocumentRoles(roleList, meta);
    }

    @Override
    public DocumentRoles getDocumentRoles(int documentId) {
        final List<DocumentRole> documentRoles = new ArrayList<>(documentRolesRepository.findByDocument_Id(documentId));

	    return new DocumentRoles(documentRoles, metaRepository.getOne(documentId));
    }
}
