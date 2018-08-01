package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExternalToLocalRoleLinkRepository extends JpaRepository<ExternalToLocalRoleLink, Integer> {

    Set<ExternalToLocalRoleLink> findByProviderId(String providerId);

    ExternalToLocalRoleLink findByProviderIdAndExternalRoleIdAndLocalRoleId(
            String providerId, String externalRoleId, int localRoleId
    );

    void deleteByProviderIdAndExternalRoleIdAndLocalRoleId(
            String providerId, String externalRoleId, int localRoleId
    );

    void deleteByProviderIdAndExternalRoleId(String providerId, String externalRoleId);

}
