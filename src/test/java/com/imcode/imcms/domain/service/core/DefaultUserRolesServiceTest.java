package com.imcode.imcms.domain.service.core;

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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserRolesServiceTest {

    @Mock
    private UserRolesRepository userRolesRepository;

    @InjectMocks
    private DefaultUserRolesService defaultUserRolesService;

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

        when(userRolesRepository.getUserRolesByUserId(user.getId())).thenReturn(userRoles);

        final List<Role> rolesByUser = defaultUserRolesService.getRolesByUser(user);

        assertThat(rolesByUser, hasSize(roles.size()));
        assertThat(rolesByUser, is(roles));

        verify(userRolesRepository, times(1)).getUserRolesByUserId(user.getId());
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

        when(userRolesRepository.getUserRolesByRoleId(role.getId())).thenReturn(userRoles);

        final List<User> usersByRole = defaultUserRolesService.getUsersByRole(role);

        assertThat(usersByRole, hasSize(users.size()));
        assertThat(usersByRole, is(users));

        verify(userRolesRepository, times(1)).getUserRolesByRoleId(role.getId());
    }
}
