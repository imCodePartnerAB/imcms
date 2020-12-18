package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.RoleNotFoundException;
import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.DocumentRolesService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.SearchDocumentService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.DocumentRole;
import imcode.server.Imcms;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Meta.Permission.NONE;

@Service
public class DefaultSearchDocumentService implements SearchDocumentService {

    private final ResolvingQueryIndex documentIndex;
    private final DocumentRolesService documentRolesService;
    private final RoleService roleService;

    DefaultSearchDocumentService(ResolvingQueryIndex documentIndex,
                                 DocumentRolesService documentRolesService,
                                 RoleService roleService) {
        this.documentIndex = documentIndex;
        this.documentRolesService = documentRolesService;
        this.roleService = roleService;
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery) {
        final UserDomainObject searchingUser = Imcms.getUser();
        final Integer roleId = searchQuery.getRoleId();

        if (null != roleId) {
            final Role selectedRole = Optional.ofNullable(roleService.getById(roleId))
                    .orElseThrow(() -> new RoleNotFoundException(roleId));

            return searchDocumentsBySelectedRole(searchQuery, searchingUser, selectedRole);
        }

        return searchDocuments(searchQuery, searchingUser);
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery) {
        return documentIndex.search(searchQuery, Imcms.getUser())
                .documentStoredFieldsList()
                .stream()
                .map(DocumentStoredFieldsDTO::new)
                .collect(Collectors.toList());
    }

    private boolean isDocumentRolesHavePermissionAndContainsRole(DocumentRoles documentRoles, Role selectedRole) {
        return documentRoles.getDocumentRoles()
                .stream()
                .filter(documentRole -> !documentRole.getPermission().getName().equals(NONE.getName()))
                .map(DocumentRole::getRole)
                .anyMatch(roleJPA -> roleJPA.getId().equals(selectedRole.getId()));
    }

    private List<DocumentStoredFieldsDTO> searchDocumentsBySelectedRole(SearchQueryDTO searchQuery,
                                                                        UserDomainObject searchingUser,
                                                                        Role selectedRole) {
        return documentIndex.search(searchQuery, searchingUser)
                .documentStoredFieldsList()
                .stream()
                .filter(documentStoredFields -> {
                    final DocumentRoles documentRoles = documentRolesService.getDocumentRoles(documentStoredFields.id());

                    return !documentRoles.hasNoRoles() &&
                            isDocumentRolesHavePermissionAndContainsRole(documentRoles, selectedRole);
                })
                .map(DocumentStoredFieldsDTO::new)
                .collect(Collectors.toList());
    }

    private List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery, UserDomainObject searchingUser) {
        return documentIndex.search(searchQuery, searchingUser)
                .documentStoredFieldsList()
                .stream()
                .map(DocumentStoredFieldsDTO::new)
                .collect(Collectors.toList());
    }
}
