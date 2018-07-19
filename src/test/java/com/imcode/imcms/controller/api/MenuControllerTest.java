package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class MenuControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    private static final int DOC_ID = 1001;
    private static final int WORKING_VERSION_NO = 0;
    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Before
    public void setUp() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @After
    public void cleanRepos() {
        menuDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Override
    protected String controllerPath() {
        return "/menus";
    }

    @Test
    public void getMenuItems_When_MenuExists_Expect_MenuItemsDtosJson() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);

        menu.getMenuItems()
                .stream()
                .map(new UnaryOperator<MenuItemDTO>() {
                    @Override
                    public MenuItemDTO apply(MenuItemDTO menuItemDTO) {
                        menuItemDTO.setTitle("headline_en");

                        menuItemDTO.getChildren()
                                .stream()
                                .map(this)
                                .collect(Collectors.toList());

                        return menuItemDTO;
                    }
                })
                .collect(Collectors.toList());

        commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_NO);

        final String expectedMenuItemDtos = asJson(menu.getMenuItems());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedMenuItemDtos);
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

        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(0, 1001);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
    }

}
