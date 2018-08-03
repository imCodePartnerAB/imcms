package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.RoleJPA;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18.
 */
class ExternalToLocalRolesLinksControllerTest extends MockingControllerTest {

    public static final String PATH = "/external-to-local-roles-links";

    @Mock
    private ExternalToLocalRoleLinkService externalToLocalRoleLinkService;

    @InjectMocks
    private ExternalToLocalRolesLinksController controller;

    @Override
    protected Object controllerToMock() {
        return controller;
    }

    @Test
    void getLinkedLocalRoles() {
        final RoleJPA role1 = new RoleJPA(1, "first");
        final RoleJPA role2 = new RoleJPA(2, "second");

        final Set<Role> expected = new HashSet<>(Arrays.asList(new RoleDTO(role1), new RoleDTO(role2)));
        final ExternalRole externalRole = new ExternalRole();

        given(externalToLocalRoleLinkService.getLinkedLocalRoles(externalRole)).willReturn(expected);

        final String response = perform(get(PATH), externalRole).getResponse();

        then(externalToLocalRoleLinkService).should().getLinkedLocalRoles(externalRole);

        final Set<Role> actual = new HashSet<>(fromJson(response, new TypeReference<Set<RoleDTO>>() {
        }));

        assertEquals(expected, actual);
    }

    @Test
    void saveLinkedLocalRoles() {
        final ExternalRole externalRole = new ExternalRole();
        externalRole.setId("id");

        final HashSet<Integer> localRolesId = new HashSet<>();
        localRolesId.add(1);

        perform(put(PATH), new ExternalToLocalRolesLinksController.ExternalRoleLinks(externalRole, localRolesId));

        then(externalToLocalRoleLinkService).should().setLinkedRoles(externalRole, localRolesId);
    }
}
