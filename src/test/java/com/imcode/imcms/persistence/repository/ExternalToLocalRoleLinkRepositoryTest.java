package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.TransactionalWebAppSpringTestConfig;
import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLink;
import com.imcode.imcms.persistence.entity.RoleJPA;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18.
 */
public class ExternalToLocalRoleLinkRepositoryTest extends TransactionalWebAppSpringTestConfig {

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

        final ExternalToLocalRoleLink link11 = repository.save(new ExternalToLocalRoleLink(
                provider1, "external-role-1", localRole1
        ));

        final ExternalToLocalRoleLink link12 = repository.save(new ExternalToLocalRoleLink(
                provider1, "external-role-2", localRole2
        ));

        final ExternalToLocalRoleLink link21 = repository.save(new ExternalToLocalRoleLink(
                provider2, "external-role-1", localRole1
        ));

        final ExternalToLocalRoleLink link22 = repository.save(new ExternalToLocalRoleLink(
                provider2, "external-role-2", localRole2
        ));

        final Set<ExternalToLocalRoleLink> byProvider1 = repository.findByProviderId(provider1);

        assertNotNull(byProvider1);
        assertFalse(byProvider1.isEmpty());

        assertTrue(Arrays.asList(link11, link12).containsAll(byProvider1));

        final Set<ExternalToLocalRoleLink> byProvider2 = repository.findByProviderId(provider2);

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

        final ExternalToLocalRoleLink link = repository.save(new ExternalToLocalRoleLink(
                provider, "external-role-1", localRole
        ));

        final List<ExternalToLocalRoleLink> all = repository.findAll();

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

        repository.save(new ExternalToLocalRoleLink(
                provider, "external-role-1", localRole
        ));

        assertFalse(repository.findAll().isEmpty());

        roleRepository.delete(localRole.getId());
        roleRepository.flush();

        assertNull(roleRepository.findOne(localRole.getId()));
        assertTrue(repository.findAll().isEmpty());
    }
}
