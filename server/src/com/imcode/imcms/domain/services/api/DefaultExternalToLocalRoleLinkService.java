package com.imcode.imcms.domain.services.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.ExternalToLocalRoleLink;
import com.imcode.imcms.domain.services.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.ExternalToLocalRoleLinkModel;
import imcode.server.Imcms;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultExternalToLocalRoleLinkService implements ExternalToLocalRoleLinkService {

    private final static Logger log = Logger.getLogger(DefaultExternalToLocalRoleLinkService.class);

    @Override
    public void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId) {

        final Set<ExternalToLocalRoleLink> linksSent = localRolesId.stream()
                .map(localRoleId -> new ExternalToLocalRoleLink(externalRole, localRoleId))
                .collect(Collectors.toSet());

        final Set<ExternalToLocalRoleLinkModel> alreadyLinkedRoles = getLinkedRoles(externalRole);

        alreadyLinkedRoles.stream()
                .filter(alreadyLinkedRole -> !linksSent.contains(removeId(alreadyLinkedRole)))
                .forEach(this::delete);

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
                .forEach(this::save);

    }

    private ExternalToLocalRoleLink removeId(ExternalToLocalRoleLinkModel roleLink) {
        return new ExternalToLocalRoleLink(
                roleLink.getProviderId(), roleLink.getExternalRoleId(), roleLink.getLocalRoleId()
        );
    }

    @Override
    public Set<RoleDomainObject> getLinkedLocalRoles(ExternalRole externalRole) {
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
                .flatMap(externalRole -> getLinkedLocalRoles(externalRole).stream())
                .collect(Collectors.toSet());
    }

    private Set<ExternalToLocalRoleLinkModel> getLinkedRoles(ExternalRole externalRole) {
        return findByProviderIdAndExternalRoleId(externalRole);
    }

    private Set<ExternalToLocalRoleLinkModel> findByProviderIdAndExternalRoleId(ExternalRole externalRole) {
        final String sql = "SELECT * from external_to_local_roles_links WHERE provider_id =? AND external_role_id =?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Set<ExternalToLocalRoleLinkModel> externalRoleLinkModels = new HashSet<>();

        try {
            preparedStatement = Imcms.getApiDataSource().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, externalRole.getProviderId());
            preparedStatement.setString(2, externalRole.getId());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                externalRoleLinkModels.add(new ExternalToLocalRoleLinkModel(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        Imcms.getServices()
                                .getImcmsAuthenticatorAndUserAndRoleMapper()
                                .getRoleById(resultSet.getInt(4))
                ));
            }

        } catch (SQLException s) {
            log.error(String.format("%s with provideId %s and Id %s", s.getMessage(),
                    externalRole.getProviderId(), externalRole.getId()));
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return externalRoleLinkModels;
    }

    private void delete(ExternalToLocalRoleLinkModel roleLinkModel) {

        final String sql = "DELETE FROM external_to_local_roles_links WHERE id=?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Imcms.getApiDataSource().getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, roleLinkModel.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException s) {
            log.error(String.format("%s delete not complete with id %d", s.getMessage(), roleLinkModel.getId()));
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<RoleDomainObject> findRolesByExternalRoleId(String id) {
        String sql = "SELECT linked_local_role_id from external_to_local_roles_links WHERE external_role_id = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        List<RoleDomainObject> roles = new ArrayList<>();
        try {
            preparedStatement = Imcms.getApiDataSource().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roles.add(Imcms.getServices()
                        .getImcmsAuthenticatorAndUserAndRoleMapper()
                        .getRoleById(resultSet.getInt(1)));
            }
        } catch (SQLException s) {
            log.error(String.format("%s dont not find roleId %s", s.getMessage(), id));
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return roles;
    }

    private void save(ExternalToLocalRoleLinkModel linkRole) {
        String sql = "INSERT INTO external_to_local_roles_links (provider_id, external_role_id, linked_local_role_id) values (?,?,?)";
        PreparedStatement preparedStatement = null;
        boolean externalHasCurrentRole = findRolesByExternalRoleId(linkRole.getExternalRoleId()).stream()
                .map(RoleDomainObject::getId)
                .map(RoleId::intValue)
                .anyMatch(id -> linkRole.getLocalRoleId().equals(id));
        try {
            if (!externalHasCurrentRole) {
                preparedStatement = Imcms.getApiDataSource().getConnection().prepareStatement(sql);
                preparedStatement.setString(1, linkRole.getProviderId());
                preparedStatement.setString(2, linkRole.getExternalRoleId());
                preparedStatement.setInt(3, linkRole.getLocalRoleId());

                preparedStatement.executeUpdate();

            }
        } catch (SQLException e) {
            log.error(String.format("%s save not completed with roleId %d", e.getMessage(), linkRole.getId()));
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
