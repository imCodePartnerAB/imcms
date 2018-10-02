package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLinkJPA;
import com.imcode.imcms.persistence.entity.RoleJPA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18.
 */
@Transactional
class ExternalToLocalRoleLinkRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private ExternalToLocalRoleLinkRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void findByProviderId() {
        final String provider1 = "provider1";
        final String provider2 = "provider2";
        final RoleJPA localRole1 = roleRepository.save(new RoleJPA("test-role-1"));
        final RoleJPA localRole2 = roleRepository.save(new RoleJPA("test-role-2"));

        final ExternalToLocalRoleLinkJPA link11 = repository.save(new ExternalToLocalRoleLinkJPA(
                provider1, "external-role-1", localRole1
        ));

        final ExternalToLocalRoleLinkJPA link12 = repository.save(new ExternalToLocalRoleLinkJPA(
                provider1, "external-role-2", localRole2
        ));

        final ExternalToLocalRoleLinkJPA link21 = repository.save(new ExternalToLocalRoleLinkJPA(
                provider2, "external-role-1", localRole1
        ));

        final ExternalToLocalRoleLinkJPA link22 = repository.save(new ExternalToLocalRoleLinkJPA(
                provider2, "external-role-2", localRole2
        ));

        final Set<ExternalToLocalRoleLinkJPA> byProvider1 = repository.findByProviderId(provider1);

        assertNotNull(byProvider1);
        assertFalse(byProvider1.isEmpty());

        assertTrue(Arrays.asList(link11, link12).containsAll(byProvider1));

        final Set<ExternalToLocalRoleLinkJPA> byProvider2 = repository.findByProviderId(provider2);

        assertNotNull(byProvider2);
        assertFalse(byProvider2.isEmpty());

        assertTrue(Arrays.asList(link21, link22).containsAll(byProvider2));
    }

    @Test
    public void CRUD() {
        repository.deleteAll();
        assertTrue(repository.findAll().isEmpty());

        final String provider = "provider";
        final RoleJPA localRole = roleRepository.save(new RoleJPA("test-role"));

        final ExternalToLocalRoleLinkJPA link = repository.save(new ExternalToLocalRoleLinkJPA(
                provider, "external-role-1", localRole
        ));

        final List<ExternalToLocalRoleLinkJPA> all = repository.findAll();

        assertFalse(all.isEmpty());
        assertEquals(all.size(), 1);
        assertEquals(all.get(0), link);

        repository.delete(link.getId());

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void onDeleteCascade_When_LinkedRoleDeleted_Expect_LinkDeletedToo() {
        final String provider = "provider";
        final RoleJPA localRole = roleRepository.save(new RoleJPA("test-role"));

        assertTrue(repository.findAll().isEmpty());

        repository.save(new ExternalToLocalRoleLinkJPA(
                provider, "external-role-1", localRole
        ));

        assertFalse(repository.findAll().isEmpty());

        roleRepository.delete(localRole.getId());
        roleRepository.flush();

        assertNull(roleRepository.findOne(localRole.getId()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void findByProviderIdAndExternalRoleIdAndLocalRoleId() {
        final String provider = "provider";
        final String externalRoleId = "external-role-1";
        final RoleJPA localRole = roleRepository.save(new RoleJPA("test-role"));

        final ExternalToLocalRoleLinkJPA link = repository.save(
                new ExternalToLocalRoleLinkJPA(provider, externalRoleId, localRole)
        );

        final ExternalToLocalRoleLinkJPA result = repository.findByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId, localRole.getId()
        );

        assertEquals(link, result);
    }

    @Test
    public void findByProviderIdAndExternalRoleId() {
        final String provider = "provider";
        final String externalRoleId = "external-role";
        final RoleJPA localRole1 = roleRepository.save(new RoleJPA("test-role-1"));
        final RoleJPA localRole2 = roleRepository.save(new RoleJPA("test-role-2"));

        final ExternalToLocalRoleLinkJPA result1 = repository.save(new ExternalToLocalRoleLinkJPA(provider, externalRoleId, localRole1));
        final ExternalToLocalRoleLinkJPA result2 = repository.save(new ExternalToLocalRoleLinkJPA(provider, externalRoleId, localRole2));

        final List<ExternalToLocalRoleLinkJPA> expected = Arrays.asList(result1, result2);

        final Set<ExternalToLocalRoleLinkJPA> actual = repository.findByProviderIdAndExternalRoleId(
                provider, externalRoleId
        );

        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void deleteByProviderIdAndExternalRoleIdAndLocalRoleId() {
        final String provider = "provider";
        final String externalRoleId = "external-role-1";
        final RoleJPA localRole = roleRepository.save(new RoleJPA("test-role"));

        repository.save(new ExternalToLocalRoleLinkJPA(provider, externalRoleId, localRole));

        ExternalToLocalRoleLinkJPA result = repository.findByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId, localRole.getId()
        );

        assertNotNull(result);

        repository.deleteByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId, localRole.getId()
        );

        result = repository.findByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId, localRole.getId()
        );

        assertNull(result);
    }

    @Test
    public void deleteByProviderIdAndExternalRoleId() {
        final String provider = "provider";
        final String externalRoleId1 = "external-role-1";
        final String externalRoleId2 = "external-role-2";
        final RoleJPA localRole1 = roleRepository.save(new RoleJPA("test-role-1"));
        final RoleJPA localRole2 = roleRepository.save(new RoleJPA("test-role-2"));

        repository.save(new ExternalToLocalRoleLinkJPA(provider, externalRoleId1, localRole1));
        repository.save(new ExternalToLocalRoleLinkJPA(provider, externalRoleId2, localRole2));

        ExternalToLocalRoleLinkJPA result1 = repository.findByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId1, localRole1.getId()
        );

        assertNotNull(result1);

        ExternalToLocalRoleLinkJPA result2 = repository.findByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId2, localRole2.getId()
        );

        assertNotNull(result2);

        repository.deleteByProviderIdAndExternalRoleId(provider, externalRoleId1);

        result1 = repository.findByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId1, localRole1.getId()
        );

        assertNull(result1);

        repository.deleteByProviderIdAndExternalRoleId(provider, externalRoleId2);

        result2 = repository.findByProviderIdAndExternalRoleIdAndLocalRoleId(
                provider, externalRoleId2, localRole2.getId()
        );

        assertNull(result2);
    }
}
