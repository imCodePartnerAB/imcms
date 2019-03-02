package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class MenuControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private MenuService menuService;

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @AfterEach
    public void cleanRepos() {
        menuDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Override
    protected String controllerPath() {
        return "/menus";
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANG_Expect_MenuItemsDtosJson() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
    }

    @Test
    public void getMenuItems_When_MenuExistAndHasModeDONOTSHOW_Expect_EmptyResult() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final Document document = documentService.get(menu.getDocId());

        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void getMenuItems_When_UserSuperAdminMenuExistInModeDONOTSHOW_Expect_MenuItemsDtosJson() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        assertTrue(Imcms.getUser().isSuperAdmin());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
    }

    @Test
    public void getMenuItems_When_MenuMissing_Expect_Expect_EmptyArray() throws Exception {
        versionDataInitializer.createData(0, 1001);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", "1")
                .param("docId", "1001");

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void postMenu_When_MenuExistWithMenuItems_Expect_Ok() throws Exception {
        final MenuDTO menuDTO = menuDataInitializer.createData(true);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
    }

    @Test
    public void postMenu_When_MenuMissing_Expect_EmptyArray() throws Exception {
        final MenuDTO menuDTO = menuDataInitializer.createData(true);

        versionDataInitializer.createData(0, 1001);
        menuService.deleteByDocId(menuDTO.getDocId());

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
    }

}
