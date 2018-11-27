package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.RolePermissionsJPA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class RoleRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void save() {
        final RoleJPA role = new RoleJPA();
        role.setName("test_name");

        roleRepository.save(role);

        assertNotNull(role.getId());
    }

    @Test
    public void saveCollection() {
        final int howMuch = 4;
        final List<RoleJPA> forSave = new ArrayList<>();

        for (int i = 0; i < howMuch; i++) {
            final RoleJPA role = new RoleJPA();
            role.setName("test_name" + i);
            forSave.add(role);
        }

        final List<RoleJPA> saved = roleRepository.save(forSave);

        assertNotNull(saved);
        assertEquals(saved.size(), forSave.size());
    }

    @Test
    public void findOne() {
        final RoleJPA role = new RoleJPA();
        role.setName("test_name");
        roleRepository.save(role);

        assertNotNull(roleRepository.findOne(role.getId()));
    }

    @Test
    public void findAll() {
        final int prev = roleRepository.findAll().size();
        final int howMuch = 4;

        for (int i = 0; i < howMuch; i++) {
            final RoleJPA role = new RoleJPA();
            role.setName("test_name" + i);
            roleRepository.save(role);
        }

        final List<RoleJPA> all = roleRepository.findAll();

        assertEquals(all.size(), howMuch + prev);
    }

    @Test
    public void createRoleWithPermissionsSpecified_Expect_Saved() {
        final RoleJPA role = roleRepository.save(new RoleJPA("test-role-name"));
        final Integer roleId = role.getId();

        final RolePermissionsJPA permissions = new RolePermissionsJPA();
        permissions.setAccessToAdminPages(true);
        permissions.setGetPasswordByEmail(true);

        role.setPermissions(permissions);

        roleRepository.save(role);

        final RolePermissionsJPA savedPermissions = roleRepository.findOne(roleId).getPermissions();

        assertNotNull(savedPermissions);
        assertEquals(roleId, savedPermissions.getRoleId());
        assertTrue(savedPermissions.isAccessToAdminPages());
        assertTrue(savedPermissions.isGetPasswordByEmail());
    }

    @Test
    public void deleteRoleWithPermissionSpecified_Expect_Deleted() {
        RoleJPA role = roleRepository.save(new RoleJPA("test-role-name"));
        final Integer roleId = role.getId();

        final RolePermissionsJPA permissions = new RolePermissionsJPA();
        permissions.setAccessToAdminPages(true);
        permissions.setGetPasswordByEmail(true);

        role.setPermissions(permissions);

        roleRepository.save(role);
        roleRepository.delete(roleId);

        assertNull(roleRepository.findOne(roleId));
    }
}
