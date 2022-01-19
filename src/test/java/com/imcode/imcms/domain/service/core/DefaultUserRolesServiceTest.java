package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserRoles;
import com.imcode.imcms.persistence.repository.UserRolesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DefaultUserRolesServiceTest {

    @Mock
    private UserRolesRepository userRolesRepository;

    @InjectMocks
    private DefaultUserRolesService userRolesService;

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

        final List<UserRoles> userRoles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new UserRoles(user, roles.get(i)))
                .collect(Collectors.toList());

        when(userRolesRepository.findUserRolesByUserId(user.getId())).thenReturn(userRoles);

        final List<Role> rolesByUser = userRolesService.getRolesByUser(user.getId());

        assertThat(rolesByUser, hasSize(roles.size()));

        verify(userRolesRepository, times(1)).findUserRolesByUserId(user.getId());
    }

    @Test
    void getRoleIdsByUser_When_RoleExists_Expect_Returned() {
        final int userRolesSize = 3;

        final User user = new User(1, "test", "test", "test@imcode.com");

        final List<RoleJPA> roles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> {
                    final RoleJPA role = new RoleJPA();
                    role.setId(i);

                    return role;
                })
                .collect(Collectors.toList());

        final List<UserRoles> userRoles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new UserRoles(user, roles.get(i)))
                .collect(Collectors.toList());

        when(userRolesRepository.findUserRolesByUserId(user.getId())).thenReturn(userRoles);

        final Set<Integer> rolesByUser = userRolesService.getRoleIdsByUser(user.getId());

        assertThat(rolesByUser, hasSize(roles.size()));
        assertThat(rolesByUser, is(roles.stream().map(RoleJPA::getId).collect(Collectors.toSet())));

        verify(userRolesRepository, times(1)).findUserRolesByUserId(user.getId());
    }

    @Test
    void getUsers_When_RoleExists_Expect_Returned() {
        final int userRolesSize = 3;

        final RoleJPA role = new RoleJPA();
        role.setId(1);

        final List<User> users = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new User(i, "test", "test", "test@imcode.com"))
                .collect(Collectors.toList());

        final List<UserRoles> userRoles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new UserRoles(users.get(i), role))
                .collect(Collectors.toList());

        when(userRolesRepository.findUserRolesByRoleId(role.getId())).thenReturn(userRoles);

        final List<UserDTO> usersByRole = userRolesService.getUsersByRole(role.getId());

        assertThat(usersByRole, hasSize(users.size()));
        assertThat(usersByRole, is(users.stream().map(UserDTO::new).collect(Collectors.toList())));

        verify(userRolesRepository, times(1)).findUserRolesByRoleId(role.getId());
    }

    @Test
    void getUserIdsByRole_When_RoleExists_Expect_Returned() {
        final int userRolesSize = 3;

        final RoleJPA role = new RoleJPA();
        role.setId(1);

        final List<User> users = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new User(i, "test", "test", "test@imcode.com"))
                .collect(Collectors.toList());

        final List<UserRoles> userRoles = IntStream.range(0, userRolesSize)
                .mapToObj(i -> new UserRoles(users.get(i), role))
                .collect(Collectors.toList());

        when(userRolesRepository.findUserRolesByRoleId(role.getId())).thenReturn(userRoles);

        final Set<Integer> userIdsByRole = userRolesService.getUserIdsByRole(role.getId());

        assertThat(userIdsByRole, hasSize(users.size()));
        assertThat(userIdsByRole, is(users.stream().map(User::getId).collect(Collectors.toSet())));

        verify(userRolesRepository, times(1)).findUserRolesByRoleId(role.getId());
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

        userRolesService.updateUserRoles(roles, user);

        then(userRolesRepository).should().deleteUserRolesByUserId(eq(userId));
        then(userRolesRepository).should().save(anyCollection());
    }
}
