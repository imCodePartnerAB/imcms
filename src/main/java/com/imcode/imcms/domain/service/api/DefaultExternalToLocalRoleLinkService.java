package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLink;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.ExternalToLocalRoleLinkRepository;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultExternalToLocalRoleLinkService implements ExternalToLocalRoleLinkService {

    private final ExternalToLocalRoleLinkRepository repository;
    private final RoleRepository roleRepository;

    DefaultExternalToLocalRoleLinkService(ExternalToLocalRoleLinkRepository repository, RoleRepository roleRepository) {
        this.repository = repository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId) {
        final Set<ExternalToLocalRoleLink> linksSent = localRolesId.stream()
                .map(localRoleId -> {
                    final RoleJPA role = roleRepository.findOne(localRoleId);
                    return new ExternalToLocalRoleLink(externalRole, role);
                })
                .collect(Collectors.toSet());

        final Set<ExternalToLocalRoleLink> alreadyLinkedRoles = getLinkedRoles(externalRole);

        alreadyLinkedRoles.stream()
                .filter(alreadyLinkedRole -> {
                    final ExternalToLocalRoleLink linkWithoutId = removeId(alreadyLinkedRole);
                    return !linksSent.contains(linkWithoutId);
                })
                .forEach(repository::delete);

        final Set<ExternalToLocalRoleLink> alreadyLinkedRolesWithoutId = alreadyLinkedRoles.stream()
                .map(this::removeId)
                .collect(Collectors.toSet());

        linksSent.stream()
                .filter(linkSent -> !alreadyLinkedRolesWithoutId.contains(linkSent))
                .forEach(repository::save);
    }

    private ExternalToLocalRoleLink removeId(ExternalToLocalRoleLink roleLink) {
        final ExternalToLocalRoleLink linkWithoutId = new ExternalToLocalRoleLink(roleLink);
        linkWithoutId.setId(null);
        return linkWithoutId;
    }

    @Override
    public void addLink(ExternalRole externalRole, int localRoleId) {
        final RoleJPA localRole = Objects.requireNonNull(roleRepository.findOne(localRoleId));
        repository.save(new ExternalToLocalRoleLink(externalRole.getProviderId(), externalRole.getId(), localRole));
    }

    @Override
    public void removeLink(ExternalRole externalRole, int localRoleId) {
        repository.deleteByProviderIdAndExternalRoleIdAndLocalRoleId(
                externalRole.getProviderId(), externalRole.getId(), localRoleId
        );
    }

    @Override
    public void removeLinks(ExternalRole externalRole) {
        repository.deleteByProviderIdAndExternalRoleId(externalRole.getProviderId(), externalRole.getId());
    }

    @Override
    public Set<Role> getLinkedLocalRoles(ExternalRole externalRole) {
        return getLinkedRoles(externalRole)
                .stream()
                .map(ExternalToLocalRoleLink::getRole)
                .map(RoleDTO::new)
                .collect(Collectors.toSet());
    }

    private Set<ExternalToLocalRoleLink> getLinkedRoles(ExternalRole externalRole) {
        return repository.findByProviderIdAndExternalRoleId(externalRole.getProviderId(), externalRole.getId());
    }
}
