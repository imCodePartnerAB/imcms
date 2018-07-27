package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.Roles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest extends MockingControllerTest {

    private static final String PATH = "/roles";

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @Override
    protected Object controllerToMock() {
        return roleController;
    }

    @Test
    void getRolesTest() throws Exception {
        final List<Role> roles = Stream.of(Roles.SUPER_ADMIN, Roles.USER_ADMIN, Roles.USER)
                .map(roleId -> new RoleDTO(roleId.getId(), roleId.getName()))
                .collect(Collectors.toList());

        given(roleService.getAll()).willReturn(roles);

        final String response = perform(get(PATH)).getResponse();

        final List<RoleDTO> receivedRoles = fromJson(response, new TypeReference<List<RoleDTO>>() {
        });

        assertNotNull(receivedRoles);
        assertTrue(receivedRoles.containsAll(roles));
        assertTrue(roles.containsAll(receivedRoles));

        then(roleService).should().getAll();
    }

}
