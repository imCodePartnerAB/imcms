package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLinkJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExternalToLocalRoleLinkRepository extends JpaRepository<ExternalToLocalRoleLinkJPA, Integer> {

    Set<ExternalToLocalRoleLinkJPA> findByProviderId(String providerId);

    ExternalToLocalRoleLinkJPA findByProviderIdAndExternalRoleIdAndLocalRoleId(
            String providerId, String externalRoleId, int localRoleId
    );

    Set<ExternalToLocalRoleLinkJPA> findByProviderIdAndExternalRoleId(
            String providerId, String externalRoleId
    );

    void deleteByProviderIdAndExternalRoleIdAndLocalRoleId(
            String providerId, String externalRoleId, int localRoleId
    );

    void deleteByProviderIdAndExternalRoleId(String providerId, String externalRoleId);

}
