package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.persistence.entity.Menu;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
@WebAppConfiguration
public class MenuControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @After
    public void cleanRepos() {
        menuDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Override
    protected String controllerPath() {
        return "/menu";
    }

    @Test
    public void getMenuItems_When_MenuExists_Expect_MenuItemsDtosJson() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("en");
        Imcms.setUser(user);
        final Menu menu = menuDataInitializer.createData(true);
        final String expectedMenuItemDtos = asJson(menuDataInitializer.getMenuItemDtoList());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuId", String.valueOf(menu.getNo()))
                .param("docId", String.valueOf(menu.getVersion().getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedMenuItemDtos);
    }

    @Test
    public void getMenuItems_When_MenuMissing_Expect_Expect_EmptyArray() throws Exception {
        versionDataInitializer.createData(0, 1001);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuId", "1")
                .param("docId", "1001");
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void postMenu_When_MenuExistWithMenuItems_Expect_Ok() throws Exception {
        final Menu menu = menuDataInitializer.createData(true);

        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuId(menu.getNo());
        menuDTO.setDocId(menu.getVersion().getDocId());
        menuDTO.setMenuItems(menuDataInitializer.getMenuItemDtoList());

        final String jsonData = asJson(menuDTO);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonData);

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void postMenu_When_MenuMissing_Expect_EmptyArray() throws Exception {
        final Menu menu = menuDataInitializer.createData(true);

        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuId(1);
        menuDTO.setDocId(menu.getVersion().getDocId());
        menuDTO.setMenuItems(menuDataInitializer.getMenuItemDtoList());

        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(0, 1001);

        final String jsonData = asJson(menuDTO);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonData);

        performRequestBuilderExpectedOk(requestBuilder);
    }

}
