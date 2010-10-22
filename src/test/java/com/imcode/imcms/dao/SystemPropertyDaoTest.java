package com.imcode.imcms.dao;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.SystemProperty;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

/**
 * 
 */
public class SystemPropertyDaoTest {

    SystemDao systemDao;

    final String DEFAULT_LANGUAGE_ID = "DefaultLanguageId";
    final String START_DOC = "startDocument";

    @BeforeClass
    public static void recreateDB() {
        Script.recreateDB();
    }

    @Before
    public void resetDBData() {
        systemDao = new SystemDao();
        systemDao.setSessionFactory(Script.createHibernateSessionFactory(SystemProperty.class));
        
        Script.runDBScripts("system_property_dao.sql");
    }


    @Test
    public void getProperties() {
        List<SystemProperty> properties = systemDao.getProperties();

        assertTrue(properties.size() > 0);
    }


    @Test
    public void getProperty() {
        SystemProperty property = getExistingProperty(START_DOC);

        assertEquals(property.getValue(), "" + 1001);
    }


    @Test
    public void savePropery() {
        SystemProperty property = getExistingProperty(DEFAULT_LANGUAGE_ID);

        property.setValue("" + 0);

        systemDao.saveProperty(property);

        SystemProperty property2 = getExistingProperty(DEFAULT_LANGUAGE_ID);

        assertEquals(property2.getValue(), "" + 0);

        property2.setValue("" + 1);

        systemDao.saveProperty(property2);

        SystemProperty property3 = getExistingProperty(DEFAULT_LANGUAGE_ID);

        assertEquals(property3.getValue(), "" + 1);
    }


    public SystemProperty getExistingProperty(String name) {
        SystemProperty property = systemDao.getProperty(name);

        assertNotNull(property);

        return property;
    }
}