package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.MenuNotExistException;
import com.imcode.imcms.persistence.entity.Menu;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @After
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Test
    public void getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("en");
        Imcms.setUser(user);
        final Menu menu = menuDataInitializer.createData(true);

        final List<MenuItemDTO> menuItemDtosOfMenu = menuService.getMenuItemsOf(menu.getNo(), menu.getVersion().getDocId());
        assertEquals(menuDataInitializer.getMenuItemDtoList(), menuItemDtosOfMenu);
    }

    @Test(expected = MenuNotExistException.class)
    public void getMenuItemsOf_When_MenuDoesntExist_Expect_MenuNotExistException() {
        versionDataInitializer.createData(0, 1001);
        menuService.getMenuItemsOf(0, 1001);
    }

    @Test
    public void saveMenuItems_When_MenuNoAndDocIdAndMenuItems_Expect_ResultNotEqualsAsCreated() {
        final Menu menu = menuDataInitializer.createData(true);
        final List<MenuItemDTO> menuItemBefore = menuDataInitializer.getMenuItemDtoList();

        menuService.saveMenuItems(menu.getNo(), menu.getVersion().getDocId(), menuDataInitializer.getMenuItemDtoListWithoutIds());

        final List<MenuItemDTO> menuItemAfter = menuDataInitializer.getMenuItemDtoList();
        assertNotEquals(menuItemBefore, menuItemAfter);
    }

    @Test(expected = MenuNotExistException.class)
    public void saveMenuItems_When_MenuDoesntExist_Expect_MenuNotExistExceptions() {
        versionDataInitializer.createData(0, 1001);
        menuService.saveMenuItems(0, 1001, new ArrayList<>());
    }
}