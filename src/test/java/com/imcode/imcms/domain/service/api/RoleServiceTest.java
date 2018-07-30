package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.dto.RolePermissionsDTO;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.RolePermissions;
import com.imcode.imcms.model.Roles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void getAllTest() {
        final List<Role> roles = roleService.getAll();
        assertLike(Roles.SUPER_ADMIN, roles.get(0));
        assertLike(Roles.USER_ADMIN, roles.get(1));
        assertLike(Roles.USER, roles.get(2));
    }

    private void assertLike(Role roleA, Role roleB) {
        assertEquals(roleA.getId(), roleB.getId());
        assertEquals(roleA.getName(), roleB.getName());
    }

    @Test
    public void getById_When_Exist_Expect_NotNull() {
        final Integer id = roleService.getAll().get(0).getId();
        assertNotNull(roleService.getById(id));
    }

    @Test
    public void save() {
        final Role saveMe = new RoleDTO("test_name_role");
        final Role saved = roleService.save(saveMe);
        final Role received = roleService.getById(saved.getId());

        assertEquals(received, saved);
    }

    @Test
    public void getById_When_RoleExistWithSpecifiedPermissions_Expect_SameReturned() {
        final Role role = roleService.save(new RoleDTO("test_name_role"));
        final Integer roleId = role.getId();

        final RolePermissions permissions = new RolePermissionsDTO();
        permissions.setRoleId(roleId);
        permissions.setAccessToAdminPages(true);
        permissions.setGetPasswordByEmail(true);

        role.setPermissions(permissions);

        roleService.save(role);

        final RolePermissions savedPermissions = roleService.getById(roleId).getPermissions();

        assertEquals(new RolePermissionsDTO(savedPermissions), new RolePermissionsDTO(permissions));
    }

    @Test
    public void saveNewRole() {
        final RoleDTO roleToSave = new RoleDTO("test-role");
        final RolePermissionsDTO permissions = new RolePermissionsDTO();
        permissions.setGetPasswordByEmail(true);
        roleToSave.setPermissions(permissions);

        final Role savedRole = roleService.saveNewRole(roleToSave);

        assertNotNull(savedRole.getId());
        assertEquals(roleToSave.getName(), savedRole.getName());

        final RolePermissions savedPermissions = savedRole.getPermissions();
        assertNotNull(savedPermissions);

        assertEquals(permissions.isGetPasswordByEmail(), savedPermissions.isGetPasswordByEmail());
        assertEquals(permissions.isAccessToAdminPages(), savedPermissions.isAccessToAdminPages());
        assertEquals(permissions.isChangeImagesInImageArchive(), savedPermissions.isChangeImagesInImageArchive());
        assertEquals(permissions.isUseImagesInImageArchive(), savedPermissions.isUseImagesInImageArchive());
    }
}
