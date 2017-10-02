package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.MenuHistory;
import com.imcode.imcms.util.datainitializer.MenuDataInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MenuHistoryRepositoryTest {

    @Autowired
    @Qualifier("com.imcode.imcms.persistence.repository.MenuHistoryRepository")
    private MenuHistoryRepository menuHistoryRepository;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    private MenuHistory menuHistory;

    @Before
    public void createMenuHistory() throws InvocationTargetException, IllegalAccessException {
        menuHistory = menuDataInitializer.createMenuHistory();
    }

    @After
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
    }

    @Test
    public void findOne_Expect_theSameMenuNoAndVersion() throws Exception {
        final MenuHistory menuPersisted = menuHistoryRepository.findOne(menuHistory.getId());
        final Version persistedVersion = menuPersisted.getVersion();

        assertEquals(menuHistory.getNo(), menuPersisted.getNo());
        assertEquals(menuHistory.getDocumentId(), persistedVersion.getDocId());
        assertEquals(menuHistory.getVersion().getNo(), persistedVersion.getNo());
    }

}
