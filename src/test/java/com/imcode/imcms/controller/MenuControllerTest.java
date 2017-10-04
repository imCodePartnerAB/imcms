package com.imcode.imcms.controller;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.service.exception.MenuNotExistException;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.util.datainitializer.MenuDataInitializer;
import com.imcode.imcms.util.datainitializer.VersionDataInitializer;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import static org.junit.Assert.*;

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
        final String expectedMenuItemDtos = asJson(menuDataInitializer.getMenuItemDTOs());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuId", String.valueOf(menu.getNo()))
                .param("docId", String.valueOf(menu.getVersion().getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedMenuItemDtos);
    }

    @Test
    public void getMenuItems_When_MenuMissing_Expect_Exception() throws Exception {
        versionDataInitializer.createData(0, 1001);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuId", "0")
                .param("docId", "1001");
        try {
            performRequestBuilderExpectedOk(requestBuilder);
            fail("NestedServletException isn't fired");
        } catch (NestedServletException e) {
            assertTrue(e.getCause() instanceof MenuNotExistException);
            assertEquals(e.getCause().getMessage(),
                    String.format("Menu with no = %d and documentId = %d does not exist!", 0, 1001));
        }
    }
}
