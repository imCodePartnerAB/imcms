package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class MenuRepositoryTest extends WebAppSpringTestConfig {

    private static final int COUNT_MENU_ITEMS = 3;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @BeforeEach
    public void setUpJdbcTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM imcms_menu");
        jdbcTemplate.execute("DELETE FROM imcms_menu_item");

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);

        final Language currentLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage();
        Imcms.setLanguage(currentLanguage);
    }

    @AfterEach
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
        jdbcTemplate.execute("DELETE FROM imcms_menu");
        jdbcTemplate.execute("DELETE FROM imcms_menu_item");
    }

    @Test
    public void findOne_When_menuWithoutMenuItems_NestedOff_Expect_theSameMenuNoAndVersion() {
        final MenuDTO menu = menuDataInitializer.createData(false, null, 0);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);
        final Version versionPersisted = menuPersisted.getVersion();

        assertEquals(menu.getMenuIndex(), menuPersisted.getNo());
        assertEquals(version.getDocId(), versionPersisted.getDocId());
        assertEquals(version.getNo(), versionPersisted.getNo());
    }

    @Test
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_NestedOff_menuWithoutMenuItems_Expect_notNullMenu() {
        final MenuDTO menu = menuDataInitializer.createData(false, null, 0);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);

        assertNotNull(menuPersisted);
    }

    @Test
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_menuWithMenuItems_NestedOn_Expect_correctItemsCapacity() {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);

        final List<MenuItem> menuItems = new ArrayList<>(menuPersisted.getMenuItems());

        assertEquals(3, menuItems.size());
    }

    @Test
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_menuWithMenuItems_NestedOn_Expect_correctItemsOrder() {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);

        final List<MenuItem> menuItems = new ArrayList<>(menuPersisted.getMenuItems());

        assertEquals("1", menuItems.get(0).getSortOrder());
        assertEquals("2", menuItems.get(1).getSortOrder());
        assertEquals("3", menuItems.get(2).getSortOrder());
    }

    @Test
    public void deleteMenuItems() {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);
        menuPersisted.getMenuItems().clear();

        menuRepository.saveAndFlush(menuPersisted);

        final Long menuItemsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM imcms_menu_item", Long.class);

        assertEquals(0, menuItemsCount.longValue());
    }

    @Test
    public void findByDocId() {
        assertTrue(menuRepository.findAll().isEmpty());

        final int docId = 1001;

        IntStream.range(0, 3).forEach((versionIndex) -> {
            versionDataInitializer.createData(versionIndex, docId);

            IntStream.range(1, 5).forEach((menuIndex) ->
                    menuDataInitializer.createData(true, menuIndex, versionIndex, docId, null, COUNT_MENU_ITEMS)
            );
        });

        final List<Menu> all = menuRepository.findAll();
        assertFalse(all.isEmpty());
        assertEquals(menuRepository.findByDocId(docId).size(), all.size());
    }

    @Test
    public void deleteByDocId() {
        assertTrue(menuRepository.findAll().isEmpty());

        final int docId = 1001;

        IntStream.range(0, 3).forEach((versionIndex) -> {
            versionDataInitializer.createData(versionIndex, docId);

            IntStream.range(1, 5).forEach((menuIndex) ->
                    menuDataInitializer.createData(true, menuIndex, versionIndex, docId, null, COUNT_MENU_ITEMS)
            );
        });

        assertFalse(menuRepository.findAll().isEmpty());

        menuRepository.deleteByDocId(docId);

        assertTrue(menuRepository.findAll().isEmpty());
    }

}
