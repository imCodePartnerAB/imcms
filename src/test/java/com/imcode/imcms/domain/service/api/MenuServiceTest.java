package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Menu;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

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
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
        final Menu menu = menuDataInitializer.createData(true);

        final List<MenuItemDTO> menuItemDtosOfMenu = menuService.getMenuItemsOf(menu.getNo(), menu.getVersion().getDocId());
        assertEquals(menuDataInitializer.getMenuItemDtoList(), menuItemDtosOfMenu);
    }

    @Test
    public void getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList() {
        versionDataInitializer.createData(0, 1001);
        final List<MenuItemDTO> menuItems = menuService.getMenuItemsOf(0, 1001);
        assertTrue(menuItems.isEmpty());
    }

    @Test
    public void saveFrom_When_MenuWithItems_Expect_SameSizeButResultsNotEquals() {
        saveFrom_Expect_SameSizeButResultsNotEquals(true);
    }

    @Test
    public void saveFrom_When_MenuDoesntExist_Expect_EmptyList() {
        saveFrom_Expect_SameSizeButResultsNotEquals(false);
    }

    private void saveFrom_Expect_SameSizeButResultsNotEquals(boolean menuExist) {
        final Menu menu = menuDataInitializer.createData(true);
        final List<MenuItemDTO> menuItemBefore = menuDataInitializer.getMenuItemDtoList();

        final MenuDTO menuDTO = menuDtoFrom(menu.getNo(), menu.getVersion().getDocId(), menuDataInitializer.getMenuItemDtoListWithoutIds());

        if (!menuExist) {
            menuDataInitializer.cleanRepositories();
            versionDataInitializer.createData(0, 1001);
        }

        menuService.saveFrom(menuDTO);

        final List<MenuItemDTO> menuItemAfter = menuDataInitializer.getMenuItemDtoList();
        assertEquals(menuItemBefore.size(), menuItemAfter.size());
        assertNotEquals(menuItemBefore, menuItemAfter);
    }

    private MenuDTO menuDtoFrom(int menuId, int docId, List<MenuItemDTO> menuItems) {
        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuId(menuId);
        menuDTO.setDocId(docId);
        menuDTO.setMenuItems(menuItems);
        return menuDTO;
    }

}