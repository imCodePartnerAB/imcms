package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.ExternalToLocalRoleLink;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLinkJPA;
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
                .map(localRoleId -> new ExternalToLocalRoleLink(externalRole, localRoleId))
                .collect(Collectors.toSet());

        final Set<ExternalToLocalRoleLinkJPA> alreadyLinkedRoles = getLinkedRoles(externalRole);

        alreadyLinkedRoles.stream()
                .filter(alreadyLinkedRole -> !linksSent.contains(removeId(alreadyLinkedRole)))
                .forEach(repository::delete);

        final Set<ExternalToLocalRoleLink> alreadyLinkedRolesWithoutId = alreadyLinkedRoles.stream()
                .map(this::removeId)
                .collect(Collectors.toSet());

        linksSent.stream()
                .filter(linkSent -> !alreadyLinkedRolesWithoutId.contains(linkSent))
                .map(from -> {
                    final ExternalToLocalRoleLinkJPA linkJPA = new ExternalToLocalRoleLinkJPA(from);
                    linkJPA.setRole(roleRepository.findOne(from.getLocalRoleId()));
                    return linkJPA;
                })
                .forEach(repository::save);
    }

    private ExternalToLocalRoleLink removeId(ExternalToLocalRoleLinkJPA roleLink) {
        return new ExternalToLocalRoleLink(
                roleLink.getProviderId(), roleLink.getExternalRoleId(), roleLink.getLocalRoleId()
        );
    }

    @Override
    public void addLink(ExternalRole externalRole, int localRoleId) {
        final RoleJPA localRole = Objects.requireNonNull(roleRepository.findOne(localRoleId));
        repository.save(new ExternalToLocalRoleLinkJPA(externalRole.getProviderId(), externalRole.getId(), localRole));
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
                .map(ExternalToLocalRoleLinkJPA::getRole)
                .map(RoleDTO::new)
                .collect(Collectors.toSet());
    }

    private Set<ExternalToLocalRoleLinkJPA> getLinkedRoles(ExternalRole externalRole) {
        return repository.findByProviderIdAndExternalRoleId(externalRole.getProviderId(), externalRole.getId());
    }

    @Override
    public Set<Role> toLinkedLocalRoles(Set<ExternalRole> externalRoles) {
        return externalRoles.stream()
                .flatMap(externalRole -> getLinkedLocalRoles(externalRole).stream())
                .collect(Collectors.toSet());
    }
}
