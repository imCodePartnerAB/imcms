package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.util.datainitializer.MenuDataInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MenuRepositoryTest {

    @Autowired
    @Qualifier("com.imcode.imcms.persistence.repository.MenuRepository")
    private MenuRepository menuRepository;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    private Menu menu;

    @Before
    public void createMenu() {
        menu = menuDataInitializer.createMenu();
    }

    @After
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
    }

    @Test
    public void findOne_Expect_theSameMenuNoAndVersion() throws Exception {
        final Menu menuPersisted = menuRepository.findOne(menu.getId());
        final Version persistedVersion = menuPersisted.getVersion();

        assertEquals(menu.getNo(), menuPersisted.getNo());
        assertEquals(menu.getDocumentId(), persistedVersion.getDocId());
        assertEquals(menu.getVersion().getNo(), persistedVersion.getNo());
    }


}
