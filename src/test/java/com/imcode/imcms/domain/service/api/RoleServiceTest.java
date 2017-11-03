package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.RoleDTO;
import imcode.server.user.RoleId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void getAllTest() {
        final List<RoleDTO> roles = roleService.getAll();
        assertLike(RoleId.SUPERADMIN, roles.get(0));
        assertLike(RoleId.USERADMIN, roles.get(1));
        assertLike(RoleId.USERS, roles.get(2));
    }

    private void assertLike(RoleId roleId, RoleDTO roleDTO) {
        assertEquals((Integer) roleId.getRoleId(), roleDTO.getId());
        assertEquals(roleId.getName(), roleDTO.getName());
    }

}
