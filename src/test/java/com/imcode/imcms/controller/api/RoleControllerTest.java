package com.imcode.imcms.controller.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.RoleDTO;
import imcode.server.user.RoleId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class RoleControllerTest extends AbstractControllerTest {

    private List<RoleDTO> roles;

    @Override
    protected String controllerPath() {
        return "/roles";
    }

    @Before
    public void setUp() throws Exception {
        roles = Stream.of(RoleId.SUPERADMIN, RoleId.USERADMIN, RoleId.USERS)
                .map(roleId -> new RoleDTO(roleId.getRoleId(), roleId.getName()))
                .collect(Collectors.toList());
    }

    @Test
    public void getRolesTest() throws Exception {
        final String rolesJson = asJson(roles);
        getAllExpectedOkAndJsonContentEquals(rolesJson);
    }

}
