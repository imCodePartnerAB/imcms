package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MenuServiceTest {

    private static final int VERSION_NO = 0;
    private static final int DOC_ID = 1001;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private Function<Menu, MenuDTO> menuToMenuDTO;

    @After
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Test
    public void getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems() {
        getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems(true);
    }

    @Test
    public void getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList() {
        getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList(true);
    }

    @Test
    public void saveFrom_When_MenuWithItems_Expect_SameSizeAndResultsEquals() {
        saveFrom_Expect_SameSizeAndResultsEquals(true);
    }

    @Test
    public void saveFrom_When_MenuDoesntExist_Expect_SameSizeAndResultsEquals() {
        saveFrom_Expect_SameSizeAndResultsEquals(false);
    }

    @Test
    public void getPublicMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems() {
        getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems(false);
    }


    @Test
    public void getPublicMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList() {
        getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList(false);
    }

    @Test
    public void getMenuByVersion_When_MenuWithoutItems_Expect_SameSizeAndResultsEquals() {
        menuDataInitializer.cleanRepositories();

        final Set<Version> versions = new HashSet<>();
        final List<MenuDTO> menuDTOS = IntStream.range(0, 4)
                .mapToObj(menuIndex -> {
                    final Menu menu = menuDataInitializer.createData(false, menuIndex);
                    versions.add(menu.getVersion());
                    return menuToMenuDTO.apply(menu);
                })
                .collect(Collectors.toList());

        final Collection<MenuDTO> allByVersion = menuService.findAllByVersion(new ArrayList<>(versions).get(0));
        assertEquals(menuDTOS.size(), allByVersion.size());
        assertTrue(allByVersion.containsAll(menuDTOS));
    }

    private void getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems(boolean isAll) {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
        final Menu menu = menuDataInitializer.createData(true);

        final List<MenuItemDTO> menuItemDtosOfMenu = isAll
                ? menuService.getMenuItemsOf(menu.getNo(), menu.getVersion().getDocId())
                : menuService.getPublicMenuItemsOf(menu.getNo(), menu.getVersion().getDocId());

        assertEquals(menuDataInitializer.getMenuItemDtoList().size(), menuItemDtosOfMenu.size());
        assertEquals(menuDataInitializer.getMenuItemDtoList().get(0).getChildren().size(), menuItemDtosOfMenu.get(0).getChildren().size());
        assertEquals(menuDataInitializer.getMenuItemDtoList().get(0).getChildren().get(0).getChildren().size(), menuItemDtosOfMenu.get(0).getChildren().get(0).getChildren().size());
    }

    private void getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList(boolean isAll) {
        versionDataInitializer.createData(VERSION_NO, DOC_ID);
        final List<MenuItemDTO> menuItems = isAll
                ? menuService.getMenuItemsOf(VERSION_NO, DOC_ID)
                : menuService.getPublicMenuItemsOf(VERSION_NO, DOC_ID);
        assertTrue(menuItems.isEmpty());
    }

    private void saveFrom_Expect_SameSizeAndResultsEquals(boolean menuExist) {
        final Menu menu = menuDataInitializer.createData(true);
        final List<MenuItemDTO> menuItemBefore = menuDataInitializer.getMenuItemDtoList();

        final MenuDTO menuDTO = menuDtoFrom(menu.getNo(), menu.getVersion().getDocId(), menuDataInitializer.getMenuItemDtoList());

        if (!menuExist) {
            menuDataInitializer.cleanRepositories();
            versionDataInitializer.createData(VERSION_NO, DOC_ID);
        }

        menuService.saveFrom(menuDTO);

        final List<MenuItemDTO> menuItemAfter = menuDataInitializer.getMenuItemDtoList();
        assertEquals(menuItemBefore.size(), menuItemAfter.size());
        assertEquals(menuItemBefore, menuItemAfter);
    }

    private MenuDTO menuDtoFrom(int menuId, int docId, List<MenuItemDTO> menuItems) {
        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuId(menuId);
        menuDTO.setDocId(docId);
        menuDTO.setMenuItems(menuItems);
        return menuDTO;
    }

}