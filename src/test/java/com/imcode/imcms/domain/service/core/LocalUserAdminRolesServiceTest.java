package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserAdminRole;
import com.imcode.imcms.persistence.repository.UserAdminRolesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalUserAdminRolesServiceTest {

    @Mock
    private UserAdminRolesRepository userRolesRepository;

    @InjectMocks
    private LocalUserAdminRolesService userRolesService;

    @Test
    void getRoles_When_UserExists_Expect_Returned() {

        final int userRolesSize = 3;

        final User user = new User(1, "test", "test", "test@imcode.com");

        final List<RoleJPA> roles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> {
                    final RoleJPA role = new RoleJPA();
                    role.setId(i);

                    return role;
                })
                .collect(Collectors.toList());

        final List<UserAdminRole> userRoles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new UserAdminRole(user, roles.get(i)))
                .collect(Collectors.toList());

        given(userRolesRepository.findUserAdminRoleByUserId(user.getId())).willReturn(userRoles);

        final List<Role> rolesByUser = userRolesService.getAdminRolesByUser(user);

        assertThat(rolesByUser, hasSize(roles.size()));

        then(userRolesRepository).should().findUserAdminRoleByUserId(user.getId());
    }

    @Test
    void getUsers_When_RoleExists_Expect_Returned() {
        final int userRolesSize = 3;

        final RoleJPA role = new RoleJPA();
        role.setId(1);

        final List<User> users = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new User(i, "test", "test", "test@imcode.com"))
                .collect(Collectors.toList());

        final List<UserAdminRole> userRoles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new UserAdminRole(users.get(i), role))
                .collect(Collectors.toList());

        given(userRolesRepository.findUserAdminRoleByRoleId(role.getId())).willReturn(userRoles);

        final List<User> usersByRole = userRolesService.getUsersByAdminRole(role);

        assertThat(usersByRole, hasSize(users.size()));
        assertThat(usersByRole, is(users));

        then(userRolesRepository).should().findUserAdminRoleByRoleId(role.getId());
    }

    @Test
    void updateUserRoles_When_UserDoesNotHaveAnyRolesYet_Expect_RolesSaved() {
        final List<RoleDTO> roles = Arrays.asList(
                new RoleDTO(1, "role-1"),
                new RoleDTO(2, "role-2"),
                new RoleDTO(3, "role-3"),
                new RoleDTO(4, "role-4")
        );

        final int userId = 42;
        final User user = mock(User.class);
        given(user.getId()).willReturn(userId);

        userRolesService.updateUserAdminRoles(roles, user);

        then(userRolesRepository).should().deleteUserAdminRoleByUserId(eq(userId));
        then(userRolesRepository).should().save(anyCollection());
    }
}