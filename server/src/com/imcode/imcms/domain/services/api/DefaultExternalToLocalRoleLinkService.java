package com.imcode.imcms.domain.services.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.ExternalToLocalRoleLink;
import com.imcode.imcms.domain.repository.ExternalToLocalRoleLinkRepository;
import com.imcode.imcms.domain.services.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.ExternalToLocalRoleLinkModel;
import imcode.server.Imcms;
import imcode.server.user.RoleDomainObject;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultExternalToLocalRoleLinkService implements ExternalToLocalRoleLinkService {

    private final static Logger log = Logger.getLogger(DefaultExternalToLocalRoleLinkService.class);
    private final ExternalToLocalRoleLinkRepository externalToLocalRoleLinkRepository;

    public DefaultExternalToLocalRoleLinkService(ExternalToLocalRoleLinkRepository externalToLocalRoleLinkRepository) {
        this.externalToLocalRoleLinkRepository = externalToLocalRoleLinkRepository;
    }

    @Override
    public void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId) throws SQLException {

        final Set<ExternalToLocalRoleLink> linksSent = localRolesId.stream()
                .map(localRoleId -> new ExternalToLocalRoleLink(externalRole, localRoleId))
                .collect(Collectors.toSet());

        final Set<ExternalToLocalRoleLinkModel> alreadyLinkedRoles = getLinkedRoles(externalRole);

        for (ExternalToLocalRoleLinkModel alreadyLinkedRole : alreadyLinkedRoles) {
            if (!linksSent.contains(removeId(alreadyLinkedRole))) {
                externalToLocalRoleLinkRepository.delete(alreadyLinkedRole);
            }
        }

        final Set<ExternalToLocalRoleLink> alreadyLinkedRolesWithoutId = alreadyLinkedRoles.stream()
                .map(this::removeId)
                .collect(Collectors.toSet());

        linksSent.stream()
                .filter(linkSent -> !alreadyLinkedRolesWithoutId.contains(linkSent))
                .map(from -> {
                    final ExternalToLocalRoleLinkModel linkModel = new ExternalToLocalRoleLinkModel(from);
                    linkModel.setRole(Imcms.getServices()
                            .getImcmsAuthenticatorAndUserAndRoleMapper()
                            .getRoleById(from.getLocalRoleId()));
                    return linkModel;
                })
                .forEach(linkRole -> {
                    try {
                        externalToLocalRoleLinkRepository.save(linkRole);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

    }

    private ExternalToLocalRoleLink removeId(ExternalToLocalRoleLinkModel roleLink) {
        return new ExternalToLocalRoleLink(
                roleLink.getProviderId(), roleLink.getExternalRoleId(), roleLink.getLocalRoleId()
        );
    }

    @Override
    public Set<RoleDomainObject> getLinkedLocalRoles(ExternalRole externalRole) throws SQLException {
        return getLinkedRoles(externalRole)
                .stream()
                .map(ExternalToLocalRoleLinkModel::getRole)
                .map(role -> {
                    RoleDomainObject mapRole = new RoleDomainObject();
                    mapRole.setId(role.getId());
                    mapRole.setName(role.getName());
                    Arrays.asList(role.getPermissions()).forEach(mapRole::addPermission);
                    return mapRole;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<RoleDomainObject> toLinkedLocalRoles(Set<ExternalRole> externalRoles) {
        return externalRoles.stream()
                .flatMap(externalRole -> {
                    try {
                        return getLinkedLocalRoles(externalRole).stream();
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .collect(Collectors.toSet());
    }

    private Set<ExternalToLocalRoleLinkModel> getLinkedRoles(ExternalRole externalRole) throws SQLException {
        return externalToLocalRoleLinkRepository.findByProviderIdAndExternalRoleId(externalRole);
    }
}
