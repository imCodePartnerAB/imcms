package com.imcode.imcms.domain.services.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.services.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.ExternalToLocalRoleLinkModel;
import imcode.server.Imcms;
import imcode.server.user.RoleDomainObject;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultExternalToLocalRoleLinkService implements ExternalToLocalRoleLinkService {

    private final static Logger log = Logger.getLogger(DefaultExternalToLocalRoleLinkService.class);

    @Override
    public void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId) {

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
        return null;
    }

    private Set<ExternalToLocalRoleLinkModel> getLinkedRoles(ExternalRole externalRole) {
        return findByProviderIdAndExternalRoleId(externalRole);
    }

    private Set<ExternalToLocalRoleLinkModel> findByProviderIdAndExternalRoleId(ExternalRole externalRole) {
        final String sql = "SELECT * from external_to_local_roles_links WHERE provider_id = ? AND external_role_id = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Set<ExternalToLocalRoleLinkModel> externalRoleLinkModels = new HashSet<>();

        try {
            preparedStatement = Imcms.getApiDataSource().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, externalRole.getProviderId());
            preparedStatement.setString(2, externalRole.getId());
            resultSet = preparedStatement.executeQuery(sql);

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
        }

        return externalRoleLinkModels;
    }
}
