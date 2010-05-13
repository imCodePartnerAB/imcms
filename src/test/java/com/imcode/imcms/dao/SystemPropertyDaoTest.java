package com.imcode.imcms.dao;

import com.imcode.imcms.DBUtils;
import com.imcode.imcms.api.SystemProperty;
import com.imcode.imcms.dao.SystemDao;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.testng.Assert.*;

import java.io.File;
import java.util.List;

/**
 * 
 */
public class SystemPropertyDaoTest {

    SystemDao systemDao;

    @BeforeClass
    public static void createDB() {
        DBUtils.recreateTestDB();
    }

    @Before
    public void resetDB() {
        DBUtils.runScriptsOnTestDB("system_property_dao.sql");
        
        systemDao = new SystemDao();
        systemDao.setSessionFactory(DBUtils.createTestDBSessionFactory(SystemProperty.class));
    }


    @Test
    public void getProperties() {
        List<SystemProperty> properties = systemDao.getProperties();

        assertTrue(properties.size() > 0);
    }


    @Test
    public void getProperty() {
        SystemProperty property = getExistingProperty("startDocument");

        assertEquals(property.getValue(), "" + 1001);
    }


    @Test
    public void savePropery() {
        SystemProperty property = getExistingProperty("languageId");

        property.setValue("" + 0);

        systemDao.saveProperty(property);

        SystemProperty property2 = getExistingProperty("languageId");

        assertEquals(property2.getValue(), "" + 0);

        property2.setValue("" + 1);

        systemDao.saveProperty(property2);

        SystemProperty property3 = getExistingProperty("languageId");

        assertEquals(property3.getValue(), "" + 1);
    }


    public SystemProperty getExistingProperty(String name) {
        SystemProperty property = systemDao.getProperty(name);

        assertNotNull(property);

        return property;
    }
}