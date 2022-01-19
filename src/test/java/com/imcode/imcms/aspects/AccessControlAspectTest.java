package com.imcode.imcms.aspects;

import com.imcode.imcms.api.exception.NoPermissionException;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.RolePermissionsDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AccessControlAspectTest {

    @Mock
    private JoinPoint joinPoint;
    @Mock
    private CheckAccess checkAccess;
    @Mock
    private AccessService accessService;

    @InjectMocks
    private AccessControlAspect accessControlAspect;

    @Test
    public void checkAccess_When_UserIsSuperAdmin_Expected_NoException() {
        final UserDomainObject user = new UserDomainObject();
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        assertDoesNotThrow(() -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    public void checkAccess_When_RoleVariableIsNotDefault_And_UserHasRequiredPermission_Expected_NoException(){
        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{AccessRoleType.DOCUMENT_EDITOR});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{});

        final RolePermissionsDTO rolePermissionsDTO = new RolePermissionsDTO();
        rolePermissionsDTO.setAccessToDocumentEditor(true);
        given(accessService.getTotalRolePermissionsByUser(user)).willReturn(rolePermissionsDTO);

        assertDoesNotThrow(() -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_RoleVariableIsNotDefault_And_UserHasNotRequiredPermission_Expected_CorrectException(){
        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{AccessRoleType.DOCUMENT_EDITOR});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{});

        final RolePermissionsDTO rolePermissionsDTO = new RolePermissionsDTO();
        rolePermissionsDTO.setAccessToDocumentEditor(false);
        given(accessService.getTotalRolePermissionsByUser(user)).willReturn(rolePermissionsDTO);

        assertThrows(NoPermissionException.class, () -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_RoleVariableHasMultipleValues_And_UserHasOnePermission_Expected_NoException(){
        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{AccessRoleType.DOCUMENT_EDITOR, AccessRoleType.ADMIN_PAGES});

        final RolePermissionsDTO rolePermissionsDTO = new RolePermissionsDTO();
        rolePermissionsDTO.setAccessToDocumentEditor(false);
        rolePermissionsDTO.setAccessToAdminPages(true);
        given(accessService.getTotalRolePermissionsByUser(user)).willReturn(rolePermissionsDTO);

        assertDoesNotThrow(() -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_DocPermissionIsNotDefault_And_FirstArgIsId_And_UserHasNotRequiredDocPermission_Expected_CorrectException(){
        final int documentId = 1;

        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{AccessContentType.IMAGE});
        given(joinPoint.getArgs()).willReturn(new Object[]{documentId});

        given(accessService.hasUserEditAccess(user, documentId, AccessContentType.IMAGE)).willReturn(false);

        assertThrows(NoPermissionException.class, () -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_RoleVariableIsNotDefault_And_DocPermissionIsNotDefault_And_UserHasOnlyDocPermission_Expected_NoException(){
        final int documentId = 1;

        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{AccessRoleType.DOCUMENT_EDITOR});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{AccessContentType.IMAGE});
        given(joinPoint.getArgs()).willReturn(new Object[]{documentId});

        final RolePermissionsDTO rolePermissionsDTO = new RolePermissionsDTO();
        rolePermissionsDTO.setAccessToAdminPages(false);
        given(accessService.getTotalRolePermissionsByUser(user)).willReturn(rolePermissionsDTO);

        given(accessService.hasUserEditAccess(user, documentId, AccessContentType.IMAGE)).willReturn(true);

        assertDoesNotThrow(() -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_DocPermissionVariableHasMultipleValues_And_UserHasOnePermission_Expected_NoException(){
        final int documentId = 1;

        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{AccessContentType.IMAGE, AccessContentType.LOOP});
        given(joinPoint.getArgs()).willReturn(new Object[]{documentId});

        given(accessService.hasUserEditAccess(user, documentId, AccessContentType.IMAGE)).willReturn(false);
        given(accessService.hasUserEditAccess(user, documentId, AccessContentType.LOOP)).willReturn(true);

        assertDoesNotThrow(() -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_FirstArgIsDocument_Expected_NoException(){
        final int documentId = 1;
        final Document document = new DocumentDTO();
        document.setId(documentId);

        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{AccessContentType.DOC_INFO});
        given(joinPoint.getArgs()).willReturn(new Object[]{document});

        given(accessService.hasUserEditAccess(user, documentId, AccessContentType.DOC_INFO)).willReturn(true);

        assertDoesNotThrow(() -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_FirstArgIsDocumentable_Expected_NoException(){
        final int documentId = 1;
        final TextDTO text = new TextDTO();
        text.setDocId(documentId);

        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{AccessContentType.TEXT});
        given(joinPoint.getArgs()).willReturn(new Object[]{text});

        given(accessService.hasUserEditAccess(user, documentId, AccessContentType.TEXT)).willReturn(true);

        assertDoesNotThrow(() -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_DocPermissionIsNotDefault_And_NoArgs_Expected_CorrectException(){
        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{AccessContentType.IMAGE});
        given(joinPoint.getArgs()).willReturn(new Object[]{});

        assertThrows(NoPermissionException.class, () -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }

    @Test
    void checkAccess_When_DocPermissionIsNotDefault_And_FirstIsWrong_Expected_CorrectException(){
        String wrongArg = "";
        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);

        given(checkAccess.role()).willReturn(new AccessRoleType[]{});
        given(checkAccess.docPermission()).willReturn(new AccessContentType[]{AccessContentType.IMAGE});
        given(joinPoint.getArgs()).willReturn(new Object[]{wrongArg});

        assertThrows(NoPermissionException.class, () -> accessControlAspect.checkAccess(joinPoint, checkAccess));
    }
}
