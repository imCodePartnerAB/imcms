package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.RolePermissionsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.SearchDocumentService;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SearchDocumentControllerTest extends MockingControllerTest {

    private static final String CONTROLLER_PATH = "/documents/search";

    @Mock
    private AccessService accessService;

    @Mock
    private SearchDocumentService searchDocumentService;

    @InjectMocks
    private SearchDocumentController searchDocumentController;

    @Override
    protected Object controllerToMock() {
        return searchDocumentController;
    }

    @Test
    void getDocuments_When_DefaultSearchQuery_And_UserIsSuperAdmin_Expect_OkAndLimitSearchIsFalse() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        given(accessService.getTotalRolePermissionsByUser(any())).willReturn(new RolePermissionsDTO());

        final SearchQueryDTO searchQuery = new SearchQueryDTO();
        perform(get(CONTROLLER_PATH), searchQuery).andExpect(status().isOk());
        then(searchDocumentService).should().searchDocuments(searchQuery, false);
    }

    @Test
    void getDocuments_When_DefaultSearchQuery_And_UserIsSuperAdmin_And_RoleIdIsNotEmpty_Expect_OkAndLimitSearchIsTrue() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        given(accessService.getTotalRolePermissionsByUser(any())).willReturn(new RolePermissionsDTO());

        final SearchQueryDTO searchQuery = new SearchQueryDTO();
        searchQuery.setRoleId(100);

        perform(get(CONTROLLER_PATH + "?roleId=" + searchQuery.getRoleId())).andExpect(status().isOk());
        then(searchDocumentService).should().searchDocuments(searchQuery, true);
    }

    @Test
    void getDocuments_When_DefaultSearchQuery_And_UserIsAdminPage_Expect_OkAndLimitSearchIsTrue() {
        final UserDomainObject user = new UserDomainObject(1);
        Imcms.setUser(user);

        final RolePermissionsDTO rolePermissionsDTO = new RolePermissionsDTO();
        rolePermissionsDTO.setAccessToAdminPages(true);

        given(accessService.getTotalRolePermissionsByUser(any())).willReturn(rolePermissionsDTO);

        final SearchQueryDTO searchQuery = new SearchQueryDTO();
        perform(get(CONTROLLER_PATH)).andExpect(status().isOk());
        then(searchDocumentService).should().searchDocuments(searchQuery, true);
    }

    @Test
    void getDocuments_When_DefaultSearchQuery_And_UserIsAdminPage_And_RoleIdIsNotEmpty_Expect_OkAndLimitSearchIsTrue() {
        final UserDomainObject user = new UserDomainObject(1);
        Imcms.setUser(user);

        final RolePermissionsDTO rolePermissionsDTO = new RolePermissionsDTO();
        rolePermissionsDTO.setAccessToAdminPages(true);

        given(accessService.getTotalRolePermissionsByUser(any())).willReturn(rolePermissionsDTO);

        final SearchQueryDTO searchQuery = new SearchQueryDTO();
        searchQuery.setRoleId(100);

        perform(get(CONTROLLER_PATH + "?roleId=" + searchQuery.getRoleId())).andExpect(status().isOk());
        then(searchDocumentService).should().searchDocuments(searchQuery, true);
    }

    @Test
    void getDocuments_When_DefaultSearchQuery_And_RoleIdIsNotEmpty_Expect_Ok_And_LimitSearchIsTrue_And_RoleIdIsNull() {
        final UserDomainObject user = new UserDomainObject(1);
        Imcms.setUser(user);

        final RolePermissionsDTO rolePermissionsDTO = new RolePermissionsDTO();
        rolePermissionsDTO.setAccessToAdminPages(false);

        given(accessService.getTotalRolePermissionsByUser(any())).willReturn(rolePermissionsDTO);

        final SearchQueryDTO searchQuery = new SearchQueryDTO();
        searchQuery.setRoleId(null);

        perform(get(CONTROLLER_PATH + "?roleId=100")).andExpect(status().isOk());
        then(searchDocumentService).should().searchDocuments(searchQuery, true);
    }
}