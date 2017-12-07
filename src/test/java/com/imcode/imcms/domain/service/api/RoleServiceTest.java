package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.Role;
import imcode.server.user.RoleId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void getAllTest() {
        final List<Role> roles = roleService.getAll();
        assertLike(RoleId.SUPERADMIN, roles.get(0));
        assertLike(RoleId.USERADMIN, roles.get(1));
        assertLike(RoleId.USERS, roles.get(2));
    }

    @Test
    public void getById_When_Exist_Expect_NotNull() {
        final Integer id = roleService.getAll().get(0).getId();
        assertNotNull(roleService.getById(id));
    }

    @Test
    public void save() {
        final Role saveMe = new RoleDTO(null, "test_name_role");
        final Role saved = roleService.save(saveMe);
        final Role received = roleService.getById(saved.getId());

        assertEquals(received, saved);
    }

    private void assertLike(RoleId roleId, Role role) {
        assertEquals((Integer) roleId.getRoleId(), role.getId());
        assertEquals(roleId.getName(), role.getName());
    }

}
