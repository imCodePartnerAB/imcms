package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.RoleDTO;
import imcode.server.user.RoleId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
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
