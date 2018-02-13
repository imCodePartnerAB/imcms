package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.security.AccessType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultAccessServiceTest {

    @Mock
    private MetaRepository metaRepository;
    @Mock
    private UserRolesService userRolesService;
    @Mock
    private UserService userService;

    @InjectMocks
    private DefaultAccessService accessService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void hasUserEditAccess_When_DocumentNotExist_Expect_False() {
        final int userId = 3;
        final int metaId = 5;
        when(metaRepository.findOne(metaId)).thenReturn(null);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(0)).getUser(userId);
    }

    @Test
    public void hasUserEditAccess_When_UserNotExist_Expect_False() {
        final int userId = 3;
        final int metaId = 5;
        when(userService.getUser(userId)).thenThrow(new UserNotExistsException(userId));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(0)).getUser(userId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasEditAccess_Expect_True() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;

        final HashMap<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.EDIT);

        final User user = new User(userId, "login", "password", "email");

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertTrue(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasNoRoleForEditAccess_Expect_False() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 0;

        final HashMap<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.EDIT);

        final User user = new User(userId, "login", "password", "email");

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(2, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasViewPermission_Expect_False() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;

        final HashMap<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.VIEW);

        final User user = new User(userId, "login", "password", "email");

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);
    }

    @Test
    public void hasUserEditAccess_When_UserHasNonePermission_Expect_False() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;

        final HashMap<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.NONE);

        final User user = new User(userId, "login", "password", "email");

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);
    }

    @Test
    public void hasUserEditAccess_When_UserHasEditAndNonePermissions_Expect_True() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithNonePermission = 2;
        final int roleIdWithEditPermission = 3;

        final HashMap<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithNonePermission, Permission.NONE);
        roleIdToPermission.put(roleIdWithEditPermission, Permission.EDIT);

        final User user = new User(userId, "login", "password", "email");

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithNonePermission, "name2"),
                new RoleDTO(roleIdWithEditPermission, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertTrue(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditImageAccessAndDocumentDoesNot_Expect_False() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;
        final User user = new User(userId, "login", "password", "email");

        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.RESTRICTED_1); // 1! <----------+
        final RestrictedPermissionJPA restrictedPermission = new RestrictedPermissionJPA(); //  | main thing
        restrictedPermission.setPermission(Permission.RESTRICTED_2); // 2! <--------------------+
        restrictedPermission.setEditImage(true);

        final Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();
        restrictedPermissions.add(restrictedPermission);

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setRestrictedPermissions(restrictedPermissions);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditImageAccessAndDocumentToo_Expect_True() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;
        final User user = new User(userId, "login", "password", "email");

        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.RESTRICTED_1);

        final RestrictedPermissionJPA restrictedPermission1 = new RestrictedPermissionJPA();
        restrictedPermission1.setPermission(Permission.RESTRICTED_1);
        restrictedPermission1.setEditImage(true);

        final RestrictedPermissionJPA restrictedPermission2 = new RestrictedPermissionJPA();
        restrictedPermission2.setPermission(Permission.RESTRICTED_2);
        restrictedPermission2.setEditImage(true);

        final Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();
        restrictedPermissions.add(restrictedPermission1);
        restrictedPermissions.add(restrictedPermission2);

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setRestrictedPermissions(restrictedPermissions);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertTrue(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditImageAccessAndDocumentAnother_Expect_False() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;
        final User user = new User(userId, "login", "password", "email");

        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.RESTRICTED_1);

        final RestrictedPermissionJPA restrictedPermission1 = new RestrictedPermissionJPA();
        restrictedPermission1.setPermission(Permission.RESTRICTED_1);
        restrictedPermission1.setEditImage(false);
        restrictedPermission1.setEditMenu(true);

        final RestrictedPermissionJPA restrictedPermission2 = new RestrictedPermissionJPA();
        restrictedPermission2.setPermission(Permission.RESTRICTED_2);
        restrictedPermission2.setEditImage(true);

        final Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();
        restrictedPermissions.add(restrictedPermission1);
        restrictedPermissions.add(restrictedPermission2);

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setRestrictedPermissions(restrictedPermissions);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditMenuAccessAndDocumentToo_Expect_True() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;
        final User user = new User(userId, "login", "password", "email");

        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.RESTRICTED_1);

        final RestrictedPermissionJPA restrictedPermission1 = new RestrictedPermissionJPA();
        restrictedPermission1.setPermission(Permission.RESTRICTED_1);
        restrictedPermission1.setEditMenu(true);

        final RestrictedPermissionJPA restrictedPermission2 = new RestrictedPermissionJPA();
        restrictedPermission2.setPermission(Permission.RESTRICTED_2);
        restrictedPermission2.setEditImage(true);

        final Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();
        restrictedPermissions.add(restrictedPermission1);
        restrictedPermissions.add(restrictedPermission2);

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setRestrictedPermissions(restrictedPermissions);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.MENU);

        assertTrue(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditLoopAccessAndDocumentToo_Expect_True() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;
        final User user = new User(userId, "login", "password", "email");

        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.RESTRICTED_1);

        final RestrictedPermissionJPA restrictedPermission1 = new RestrictedPermissionJPA();
        restrictedPermission1.setPermission(Permission.RESTRICTED_1);
        restrictedPermission1.setEditLoop(true);

        final RestrictedPermissionJPA restrictedPermission2 = new RestrictedPermissionJPA();
        restrictedPermission2.setPermission(Permission.RESTRICTED_2);
        restrictedPermission2.setEditImage(true);

        final Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();
        restrictedPermissions.add(restrictedPermission1);
        restrictedPermissions.add(restrictedPermission2);

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setRestrictedPermissions(restrictedPermissions);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.LOOP);

        assertTrue(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditTextAccessAndDocumentToo_Expect_True() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;
        final User user = new User(userId, "login", "password", "email");

        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.RESTRICTED_1);

        final RestrictedPermissionJPA restrictedPermission1 = new RestrictedPermissionJPA();
        restrictedPermission1.setPermission(Permission.RESTRICTED_1);
        restrictedPermission1.setEditText(true);

        final RestrictedPermissionJPA restrictedPermission2 = new RestrictedPermissionJPA();
        restrictedPermission2.setPermission(Permission.RESTRICTED_2);
        restrictedPermission2.setEditImage(true);

        final Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();
        restrictedPermissions.add(restrictedPermission1);
        restrictedPermissions.add(restrictedPermission2);

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setRestrictedPermissions(restrictedPermissions);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.TEXT);

        assertTrue(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditDocInfoAccessAndDocumentToo_Expect_True() {
        final int metaId = 5;
        final int userId = 3;
        final int roleIdWithPermission = 2;
        final User user = new User(userId, "login", "password", "email");

        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
        roleIdToPermission.put(roleIdWithPermission, Permission.RESTRICTED_1);

        final RestrictedPermissionJPA restrictedPermission1 = new RestrictedPermissionJPA();
        restrictedPermission1.setPermission(Permission.RESTRICTED_1);
        restrictedPermission1.setEditDocInfo(true);

        final RestrictedPermissionJPA restrictedPermission2 = new RestrictedPermissionJPA();
        restrictedPermission2.setPermission(Permission.RESTRICTED_2);
        restrictedPermission2.setEditImage(true);

        final Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();
        restrictedPermissions.add(restrictedPermission1);
        restrictedPermissions.add(restrictedPermission2);

        final Meta meta = new Meta();
        meta.setId(metaId);
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setRestrictedPermissions(restrictedPermissions);

        final List<Role> roles = Arrays.asList(new Role[]{
                new RoleDTO(1, "name1"),
                new RoleDTO(roleIdWithPermission, "name2"),
                new RoleDTO(3, "name3"),
                new RoleDTO(4, "name4"),
        });

        when(metaRepository.findOne(metaId)).thenReturn(meta);
        when(userService.getUser(userId)).thenReturn(user);
        when(userRolesService.getRolesByUser(user)).thenReturn(roles);

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, metaId, AccessType.DOC_INFO);

        assertTrue(hasUserEditAccess);
        verify(metaRepository, times(1)).findOne(metaId);
        verify(userService, times(1)).getUser(userId);
        verify(userRolesService, times(1)).getRolesByUser(user);

    }

}
