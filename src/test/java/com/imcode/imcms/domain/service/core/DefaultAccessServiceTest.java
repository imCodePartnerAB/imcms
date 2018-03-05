package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.DocumentRoles;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import com.imcode.imcms.persistence.repository.DocumentRolesRepository;
import com.imcode.imcms.security.AccessType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAccessServiceTest {

    private static final int userId = 1;
    private static final int documentId = 1001;

    @Mock
    private UserService userService;

    @Mock
    private DocumentRolesRepository documentRolesRepository;

    @InjectMocks
    private DefaultAccessService accessService;

    @Test
    public void hasUserEditAccess_When_DocumentNotExist_Expect_False() {
        testHasUserEditAccessWhenDocumentRolesListIsEmpty();
    }

    @Test
    public void hasUserEditAccess_When_UserHasNoRoleForEditAccess_Expect_False() {
        testHasUserEditAccessWhenDocumentRolesListIsEmpty();
    }

    @Test
    public void hasUserEditAccess_When_UserNotExist_Expect_False() {
        when(userService.getUser(userId)).thenThrow(new UserNotExistsException(userId));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(0))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasEditAccess_Expect_True() {
        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setPermission(Permission.EDIT);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertTrue(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasViewPermission_Expect_False() {
        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setPermission(Permission.VIEW);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasNonePermission_Expect_False() {
        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setPermission(Permission.NONE);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasEditAndNonePermissions_Expect_True() {
        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setPermission(Permission.EDIT);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setPermission(Permission.NONE);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Arrays.asList(firstDocumentRoles, secondDocumentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertTrue(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditImageAccessAndDocumentDoesNot_Expect_False() {
        final RestrictedPermissionJPA restrictedPermission = new RestrictedPermissionJPA();
        restrictedPermission.setPermission(Permission.RESTRICTED_2);
        restrictedPermission.setEditImage(true);

        final Meta meta = new Meta();
        meta.setRestrictedPermissions(new HashSet<>(Collections.singletonList(restrictedPermission)));

        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setDocument(meta);
        documentRoles.setPermission(Permission.RESTRICTED_1);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditImageAccessAndDocumentToo_Expect_True() {
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
        meta.setRestrictedPermissions(restrictedPermissions);

        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setDocument(meta);
        documentRoles.setPermission(Permission.RESTRICTED_1);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertTrue(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditImageAccessAndDocumentAnother_Expect_False() {
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
        meta.setRestrictedPermissions(restrictedPermissions);

        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setDocument(meta);
        documentRoles.setPermission(Permission.RESTRICTED_1);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditMenuAccessAndDocumentToo_Expect_True() {
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
        meta.setId(documentId);
        meta.setRestrictedPermissions(restrictedPermissions);

        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setDocument(meta);
        documentRoles.setPermission(Permission.RESTRICTED_1);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.MENU);

        assertTrue(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditLoopAccessAndDocumentToo_Expect_True() {
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
        meta.setId(documentId);
        meta.setRestrictedPermissions(restrictedPermissions);

        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setDocument(meta);
        documentRoles.setPermission(Permission.RESTRICTED_1);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.LOOP);

        assertTrue(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditTextAccessAndDocumentToo_Expect_True() {
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
        meta.setId(documentId);
        meta.setRestrictedPermissions(restrictedPermissions);

        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setDocument(meta);
        documentRoles.setPermission(Permission.RESTRICTED_1);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.TEXT);

        assertTrue(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void hasUserEditAccess_When_UserHasRestrictedEditDocInfoAccessAndDocumentToo_Expect_True() {
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
        meta.setId(documentId);
        meta.setRestrictedPermissions(restrictedPermissions);

        final DocumentRoles documentRoles = new DocumentRoles();
        documentRoles.setDocument(meta);
        documentRoles.setPermission(Permission.RESTRICTED_1);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.singletonList(documentRoles));

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.DOC_INFO);

        assertTrue(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void getEditPermission_When_UserHasNoRoles_Expect_ViewPermission() {
        testWhenDocumentRolesListIsEmpty();
    }

    @Test
    public void getEditPermission_When_DocumentHasNotRoles_Expect_ViewPermission() {
        testWhenDocumentRolesListIsEmpty();
    }

    @Test
    public void getEditPermission_When_UserAndDocumentHaveEditPermission_Expect_EditPermission() {
        final Permission editPermissionEnum = Permission.EDIT;

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setPermission(Permission.VIEW);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setPermission(editPermissionEnum);

        final DocumentRoles thirdDocumentRoles = new DocumentRoles();
        thirdDocumentRoles.setPermission(Permission.RESTRICTED_1);

        final DocumentRoles fourthDocumentRoles = new DocumentRoles();
        fourthDocumentRoles.setPermission(Permission.RESTRICTED_2);

        final List<DocumentRoles> documentRolesList = Arrays.asList(
                firstDocumentRoles, secondDocumentRoles, thirdDocumentRoles, fourthDocumentRoles
        );

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(editPermissionEnum));
        assertTrue(editPermission.isEditText());
        assertTrue(editPermission.isEditMenu());
        assertTrue(editPermission.isEditImage());
        assertTrue(editPermission.isEditLoop());
        assertTrue(editPermission.isEditDocInfo());

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void getEditPermission_When_UserAndDocumentHaveOnlyViewPermission_Expect_ViewPermissionReturned() {
        final Permission viewPermission = Permission.VIEW;

        final Meta document = new Meta();
        document.setRestrictedPermissions(Collections.emptySet());

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setDocument(document);
        firstDocumentRoles.setPermission(viewPermission);

        final List<DocumentRoles> documentRolesList = Collections.singletonList(firstDocumentRoles);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(viewPermission));
        assertFalse(editPermission.isEditText());
        assertFalse(editPermission.isEditMenu());
        assertFalse(editPermission.isEditImage());
        assertFalse(editPermission.isEditLoop());
        assertFalse(editPermission.isEditDocInfo());

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void getEditPermission_When_UserAndDocumentHaveRestricted1AndRestricted2_Expect_UnionOfThem() {
        final Permission restricted1 = Permission.RESTRICTED_1;
        final Permission restricted2 = Permission.RESTRICTED_2;

        final Meta document = new Meta();

        final RestrictedPermissionJPA firstPermission = new RestrictedPermissionJPA();
        firstPermission.setPermission(restricted1);
        firstPermission.setEditText(true);
        firstPermission.setEditMenu(false);
        firstPermission.setEditImage(true);
        firstPermission.setEditLoop(false);
        firstPermission.setEditDocInfo(false);

        final RestrictedPermissionJPA secondPermission = new RestrictedPermissionJPA();
        secondPermission.setPermission(restricted2);
        secondPermission.setEditText(true);
        secondPermission.setEditMenu(false);
        secondPermission.setEditImage(false);
        secondPermission.setEditLoop(true);
        secondPermission.setEditDocInfo(false);

        document.setRestrictedPermissions(new HashSet<>(Arrays.asList(firstPermission, secondPermission)));

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setDocument(document);
        firstDocumentRoles.setPermission(Permission.VIEW);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setDocument(document);
        secondDocumentRoles.setPermission(restricted1);

        final DocumentRoles thirdDocumentRoles = new DocumentRoles();
        thirdDocumentRoles.setDocument(document);
        thirdDocumentRoles.setPermission(restricted2);

        final List<DocumentRoles> documentRolesList = Arrays.asList(
                firstDocumentRoles, secondDocumentRoles, thirdDocumentRoles
        );

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(restricted1));
        assertTrue(editPermission.isEditText());
        assertFalse(editPermission.isEditMenu());
        assertTrue(editPermission.isEditImage());
        assertTrue(editPermission.isEditLoop());
        assertFalse(editPermission.isEditDocInfo());

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void getEditPermission_When_UserAndDocumentHaveRestricted1Permission_Expect_Permission1Returned() {
        testRestrictedPermissionWhenUserAndDocumentHasCorrespondingOne(Permission.RESTRICTED_1);
    }

    @Test
    public void getEditPermission_When_UserAndDocumentHaveRestricted2Permission_Expect_Permission2Returned() {
        testRestrictedPermissionWhenUserAndDocumentHasCorrespondingOne(Permission.RESTRICTED_2);
    }

    @Test
    public void getEditPermission_When_UserHasRestricted1AndRestricted2ButDocumentDoesNotBoth_Expect_ViewPermission() {
        final Permission viewPermission = Permission.VIEW;

        final Meta document = new Meta();
        document.setRestrictedPermissions(Collections.emptySet());

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setDocument(document);
        firstDocumentRoles.setPermission(viewPermission);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setDocument(document);
        secondDocumentRoles.setPermission(Permission.RESTRICTED_1);

        final DocumentRoles thirdDocumentRoles = new DocumentRoles();
        thirdDocumentRoles.setDocument(document);
        thirdDocumentRoles.setPermission(Permission.RESTRICTED_2);

        final List<DocumentRoles> documentRolesList = Arrays.asList(
                firstDocumentRoles, secondDocumentRoles, thirdDocumentRoles
        );

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(viewPermission));
        assertFalse(editPermission.isEditText());
        assertFalse(editPermission.isEditMenu());
        assertFalse(editPermission.isEditImage());
        assertFalse(editPermission.isEditLoop());
        assertFalse(editPermission.isEditDocInfo());

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    @Test
    public void getEditPerm_When_UserHasRestricted1AndRestricted2ButDocumentHasOnlyRestricted1_Expect_Restricted1() {
        testWhenUserHasAllOfThemButDocumentHasOnlySpecified(Permission.RESTRICTED_1);
    }

    @Test
    public void getEditPerm_When_UserHasRestricted1AndRestricted2ButDocumentHasOnlyRestricted2_Expect_Restricted2() {
        testWhenUserHasAllOfThemButDocumentHasOnlySpecified(Permission.RESTRICTED_2);
    }

    @Test
    public void getEditPermission_When_UserHasRestricted1ButDocumentHasRestricted1AndRestricted2_Expect_Restricted1() {
        testWhenDocumentHasAllOfThemButUserHasOnlySpecified(Permission.RESTRICTED_1);
    }

    @Test
    public void getEditPermission_When_UserHasRestricted2ButDocumentHasRestricted1AndRestricted2_Expect_Restricted2() {
        testWhenDocumentHasAllOfThemButUserHasOnlySpecified(Permission.RESTRICTED_2);
    }

    @Test
    public void getEditPermission_When_UserHasRestricted1ButDocumentDoesNotHaveBoth_Expect_View() {
        testWhenUserHasOneSpecifiedButDocumentDoesNotHaveBoth(Permission.RESTRICTED_1);
    }

    @Test
    public void getEditPermission_When_UserHasRestricted2ButDocumentDoesNotHaveBoth_Expect_View() {
        testWhenUserHasOneSpecifiedButDocumentDoesNotHaveBoth(Permission.RESTRICTED_2);
    }

    private void testWhenUserHasOneSpecifiedButDocumentDoesNotHaveBoth(Permission permissionEnum) {
        final Meta document = new Meta();
        document.setRestrictedPermissions(Collections.emptySet());

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setDocument(document);
        firstDocumentRoles.setPermission(Permission.VIEW);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setDocument(document);
        secondDocumentRoles.setPermission(permissionEnum);

        final List<DocumentRoles> documentRolesList = Arrays.asList(
                firstDocumentRoles, secondDocumentRoles
        );

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(Permission.VIEW));
        assertFalse(editPermission.isEditText());
        assertFalse(editPermission.isEditMenu());
        assertFalse(editPermission.isEditImage());
        assertFalse(editPermission.isEditLoop());
        assertFalse(editPermission.isEditDocInfo());

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    private void testWhenDocumentHasAllOfThemButUserHasOnlySpecified(Permission permissionEnum) {
        final Meta document = new Meta();

        final Map<Permission, RestrictedPermissionJPA> permissionMap = new HashMap<>();

        final RestrictedPermissionJPA firstPermission = new RestrictedPermissionJPA();
        firstPermission.setPermission(Permission.RESTRICTED_1);
        firstPermission.setEditText(true);
        firstPermission.setEditMenu(false);
        firstPermission.setEditImage(true);
        firstPermission.setEditLoop(false);
        firstPermission.setEditDocInfo(false);

        final RestrictedPermissionJPA secondPermission = new RestrictedPermissionJPA();
        secondPermission.setPermission(Permission.RESTRICTED_2);
        secondPermission.setEditText(true);
        secondPermission.setEditMenu(false);
        secondPermission.setEditImage(false);
        secondPermission.setEditLoop(true);
        secondPermission.setEditDocInfo(false);

        permissionMap.put(Permission.RESTRICTED_1, firstPermission);
        permissionMap.put(Permission.RESTRICTED_2, secondPermission);

        document.setRestrictedPermissions(new HashSet<>(permissionMap.values()));

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setDocument(document);
        firstDocumentRoles.setPermission(Permission.VIEW);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setDocument(document);
        secondDocumentRoles.setPermission(permissionEnum);

        final List<DocumentRoles> documentRolesList = Arrays.asList(
                firstDocumentRoles, secondDocumentRoles
        );

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        final RestrictedPermissionJPA actualRestrictedPermission = permissionMap.get(permissionEnum);

        assertThat(editPermission.getPermission(), is(permissionEnum));
        assertThat(editPermission.isEditText(), is(actualRestrictedPermission.isEditText()));
        assertThat(editPermission.isEditMenu(), is(actualRestrictedPermission.isEditMenu()));
        assertThat(editPermission.isEditImage(), is(actualRestrictedPermission.isEditImage()));
        assertThat(editPermission.isEditLoop(), is(actualRestrictedPermission.isEditLoop()));
        assertThat(editPermission.isEditDocInfo(), is(actualRestrictedPermission.isEditDocInfo()));

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    private void testWhenUserHasAllOfThemButDocumentHasOnlySpecified(Permission permissionEnum) {
        final Meta document = new Meta();

        final RestrictedPermissionJPA firstPermission = new RestrictedPermissionJPA();
        firstPermission.setPermission(permissionEnum);
        firstPermission.setEditText(true);
        firstPermission.setEditMenu(false);
        firstPermission.setEditImage(true);
        firstPermission.setEditLoop(false);
        firstPermission.setEditDocInfo(false);

        document.setRestrictedPermissions(new HashSet<>(Collections.singletonList(firstPermission)));

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setDocument(document);
        firstDocumentRoles.setPermission(Permission.VIEW);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setDocument(document);
        secondDocumentRoles.setPermission(Permission.RESTRICTED_1);

        final DocumentRoles thirdDocumentRoles = new DocumentRoles();
        thirdDocumentRoles.setDocument(document);
        thirdDocumentRoles.setPermission(Permission.RESTRICTED_2);

        final List<DocumentRoles> documentRolesList = Arrays.asList(
                firstDocumentRoles, secondDocumentRoles, thirdDocumentRoles
        );

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(permissionEnum));
        assertThat(editPermission.isEditText(), is(firstPermission.isEditText()));
        assertThat(editPermission.isEditMenu(), is(firstPermission.isEditMenu()));
        assertThat(editPermission.isEditImage(), is(firstPermission.isEditImage()));
        assertThat(editPermission.isEditLoop(), is(firstPermission.isEditLoop()));
        assertThat(editPermission.isEditDocInfo(), is(firstPermission.isEditDocInfo()));

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    private void testRestrictedPermissionWhenUserAndDocumentHasCorrespondingOne(Permission permissionEnum) {
        final Meta document = new Meta();

        final RestrictedPermissionJPA permissionJPA = new RestrictedPermissionJPA();
        permissionJPA.setPermission(permissionEnum);
        permissionJPA.setEditText(true);
        permissionJPA.setEditMenu(false);
        permissionJPA.setEditImage(true);
        permissionJPA.setEditLoop(false);
        permissionJPA.setEditDocInfo(false);

        document.setRestrictedPermissions(new HashSet<>(Collections.singletonList(permissionJPA)));

        final DocumentRoles firstDocumentRoles = new DocumentRoles();
        firstDocumentRoles.setDocument(document);
        firstDocumentRoles.setPermission(Permission.VIEW);

        final DocumentRoles secondDocumentRoles = new DocumentRoles();
        secondDocumentRoles.setDocument(document);
        secondDocumentRoles.setPermission(permissionEnum);

        final List<DocumentRoles> documentRolesList = Arrays.asList(firstDocumentRoles, secondDocumentRoles);

        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(documentRolesList);

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(permissionEnum));
        assertThat(editPermission.isEditText(), is(permissionJPA.isEditText()));
        assertThat(editPermission.isEditMenu(), is(permissionJPA.isEditMenu()));
        assertThat(editPermission.isEditImage(), is(permissionJPA.isEditImage()));
        assertThat(editPermission.isEditLoop(), is(permissionJPA.isEditLoop()));
        assertThat(editPermission.isEditDocInfo(), is(permissionJPA.isEditDocInfo()));

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    private void testWhenDocumentRolesListIsEmpty() {
        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.emptyList());

        final RestrictedPermission editPermission = accessService.getEditPermission(userId, documentId);

        assertThat(editPermission.getPermission(), is(Permission.VIEW));
        assertFalse(editPermission.isEditText());
        assertFalse(editPermission.isEditMenu());
        assertFalse(editPermission.isEditImage());
        assertFalse(editPermission.isEditLoop());
        assertFalse(editPermission.isEditDocInfo());

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }

    private void testHasUserEditAccessWhenDocumentRolesListIsEmpty() {
        when(documentRolesRepository.getDocumentRolesByDocIdAndUserId(userId, documentId))
                .thenReturn(Collections.emptyList());

        final boolean hasUserEditAccess = accessService.hasUserEditAccess(userId, documentId, AccessType.IMAGE);

        assertFalse(hasUserEditAccess);

        verify(userService, times(1)).getUser(userId);

        verify(documentRolesRepository, times(1))
                .getDocumentRolesByDocIdAndUserId(userId, documentId);
    }
}
