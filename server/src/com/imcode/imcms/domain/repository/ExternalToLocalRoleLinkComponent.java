package com.imcode.imcms.domain.repository;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.model.ExternalToLocalRoleLinkModel;
import imcode.server.Imcms;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExternalToLocalRoleLinkComponent {

    private final static Logger log = LogManager.getLogger(ExternalToLocalRoleLinkComponent.class);
    private final Connection connection;


    public ExternalToLocalRoleLinkComponent(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    public Set<ExternalToLocalRoleLinkModel> findByProviderIdAndExternalRoleId(ExternalRole externalRole) throws SQLException {
        final String sql = "SELECT * from external_to_local_roles_links WHERE provider_id =? AND external_role_id =?";
        ResultSet resultSet = null;
        Set<ExternalToLocalRoleLinkModel> externalRoleLinkModels = new HashSet<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, externalRole.getProviderId());
            preparedStatement.setString(2, externalRole.getId());
            resultSet = preparedStatement.executeQuery();
            connection.commit();

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
            connection.rollback();
            log.error(String.format("%s with provideId %s and Id %s", s.getMessage(),
                    externalRole.getProviderId(), externalRole.getId()));
        } finally {
            connection.setAutoCommit(true);
            if (resultSet != null) resultSet.close();
        }

        return externalRoleLinkModels;
    }

    public void delete(ExternalToLocalRoleLinkModel roleLinkModel) throws SQLException {

        final String sql = "DELETE FROM external_to_local_roles_links WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, roleLinkModel.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException s) {
            connection.rollback();
            log.error(String.format("%s delete not complete with id %d", s.getMessage(), roleLinkModel.getId()));
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public List<RoleDomainObject> findRolesByExternalRoleId(String id) throws SQLException {
        String sql = "SELECT linked_local_role_id from external_to_local_roles_links WHERE external_role_id = ?";
        ResultSet resultSet = null;
        List<RoleDomainObject> roles = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();
            connection.commit();
            while (resultSet.next()) {
                roles.add(Imcms.getServices()
                        .getImcmsAuthenticatorAndUserAndRoleMapper()
                        .getRoleById(resultSet.getInt(1)));
            }
        } catch (SQLException s) {
            connection.rollback();
            log.error(String.format("%s dont not find roleId %s", s.getMessage(), id));
        } finally {
            connection.setAutoCommit(true);
            if (resultSet != null) resultSet.close();
        }

        return roles;
    }

    public void save(ExternalToLocalRoleLinkModel linkRole) throws SQLException {
        String sql = "INSERT INTO external_to_local_roles_links (provider_id, external_role_id, linked_local_role_id) values (?,?,?)";
        boolean externalHasCurrentRole = findRolesByExternalRoleId(linkRole.getExternalRoleId()).stream()
                .map(RoleDomainObject::getId)
                .map(RoleId::intValue)
                .anyMatch(id -> linkRole.getLocalRoleId().equals(id));
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            if (!externalHasCurrentRole) {
                preparedStatement.setString(1, linkRole.getProviderId());
                preparedStatement.setString(2, linkRole.getExternalRoleId());
                preparedStatement.setInt(3, linkRole.getLocalRoleId());

                preparedStatement.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            connection.rollback();
            log.error(String.format("%s save not completed with roleId %d", e.getMessage(), linkRole.getId()));
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
