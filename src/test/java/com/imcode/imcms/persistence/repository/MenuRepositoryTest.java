package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MenuRepositoryTest {

    @Autowired
    @Qualifier("com.imcode.imcms.persistence.repository.MenuRepository")
    private MenuRepository menuRepository;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    @Qualifier("dataSourceWithAutoCommit")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Before
    public void setUpJdbcTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM imcms_menu");
        jdbcTemplate.execute("DELETE FROM imcms_menu_item");

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @After
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
        jdbcTemplate.execute("DELETE FROM imcms_menu");
        jdbcTemplate.execute("DELETE FROM imcms_menu_item");
    }

    @Test
    public void findOne_When_menuWithoutMenuItems_Expect_theSameMenuNoAndVersion() {
        final MenuDTO menu = menuDataInitializer.createData(false);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);
        final Version versionPersisted = menuPersisted.getVersion();

        assertEquals(menu.getMenuIndex(), menuPersisted.getNo());
        assertEquals(version.getDocId(), versionPersisted.getDocId());
        assertEquals(version.getNo(), versionPersisted.getNo());
    }

    @Test
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_menuWithoutMenuItems_Expect_notNullMenu() {
        final MenuDTO menu = menuDataInitializer.createData(false);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);

        assertNotNull(menuPersisted);
    }

    @Test
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_menuWithMenuItems_Expect_correctItemsCapacity() {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);

        final List<MenuItem> menuItems = menuPersisted.getMenuItems();

        assertEquals(2, menuItems.size());
        assertEquals(3, menuItems.get(0).getChildren().size());
        assertEquals(3, menuItems.get(0).getChildren().get(0).getChildren().size());
    }

    @Test
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_menuWithMenuItems_Expect_correctItemsOrder() {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);

        final List<MenuItem> menuItems = menuPersisted.getMenuItems();

        assertEquals(1, menuItems.get(0).getSortOrder().intValue());
        assertEquals(2, menuItems.get(1).getSortOrder().intValue());
        assertEquals(1, menuItems.get(0).getChildren().get(0).getSortOrder().intValue());
        assertEquals(2, menuItems.get(0).getChildren().get(1).getSortOrder().intValue());
        assertEquals(3, menuItems.get(0).getChildren().get(2).getSortOrder().intValue());
    }

    @Test
    public void deleteMenuItems() {
        final MenuDTO menu = menuDataInitializer.createData(true);
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

        IntStream.range(0, 10).forEach((versionIndex) -> {
            versionDataInitializer.createData(versionIndex, docId);

            IntStream.range(1, 10).forEach((menuIndex) ->
                    menuDataInitializer.createData(true, menuIndex, versionIndex, docId)
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

        IntStream.range(0, 10).forEach((versionIndex) -> {
            versionDataInitializer.createData(versionIndex, docId);

            IntStream.range(1, 10).forEach((menuIndex) ->
                    menuDataInitializer.createData(true, menuIndex, versionIndex, docId)
            );
        });

        assertFalse(menuRepository.findAll().isEmpty());

        menuRepository.deleteByDocId(docId);

        assertTrue(menuRepository.findAll().isEmpty());
    }

}
