package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
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
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
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

    @Before
    public void setUpJdbcTemplate() throws SQLException {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM imcms_menu");
        jdbcTemplate.execute("DELETE FROM imcms_menu_item");

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @After
    public void cleanUpData() throws SQLException {
        menuDataInitializer.cleanRepositories();
        jdbcTemplate.execute("DELETE FROM imcms_menu");
        jdbcTemplate.execute("DELETE FROM imcms_menu_item");
    }

    @Test
    public void findOne_When_menuWithoutMenuItems_Expect_theSameMenuNoAndVersion() throws Exception {
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
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_menuWithMenuItems_Expect_correctItemsCapacity()
            throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);

        final List<MenuItem> menuItems = menuPersisted.getMenuItems();

        assertEquals(2, menuItems.size());
        assertEquals(3, menuItems.get(0).getChildren().size());
        assertEquals(3, menuItems.get(0).getChildren().get(0).getChildren().size());
    }

    @Test
    public void findByNoAndVersionAndFetchMenuItemsEagerly_When_menuWithMenuItems_Expect_correctItemsOrder()
            throws Exception {
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
    public void deleteMenuItems()
            throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final Version version = menuDataInitializer.getVersion();

        final Menu menuPersisted = menuRepository.findByNoAndVersionAndFetchMenuItemsEagerly(menu.getMenuIndex(), version);
        for (Iterator<MenuItem> iterator = menuPersisted.getMenuItems().iterator(); iterator.hasNext(); ) {
            MenuItem projectEntity = iterator.next();
            projectEntity.setMenu(null);
            iterator.remove();
        }

        menuRepository.saveAndFlush(menuPersisted);

        final Long menuItemsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM imcms_menu_item", Long.class);

        assertEquals(0, menuItemsCount.longValue());
    }


}
