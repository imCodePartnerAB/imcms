package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.service.SystemPropertyService;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class SystemPropertyServiceTest {

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Test
    public void findByName_When_PropertyExist_Expect_CorrectEntity() {

        List<SystemProperty> properties = systemPropertyService.findAll();
        assertEquals(systemPropertyService.findByName("StartDocument"), properties.get(0));
        assertEquals(systemPropertyService.findByName("SessionCounter"), properties.get(1));

    }

    @Test
    public void findByName_When_PropertyNotExist_Expect_CorrectException() {
        assertNull("NullPointerException", systemPropertyService.findByName("NotStartDocument"));
    }

    @Test
    public void findById_When_PropertyExist_Expect_CorrectEntity() {
        List<SystemProperty> properties = systemPropertyService.findAll();
        assertTrue(!properties.isEmpty());
        int id1 = 0;
        int id2 = 2;
        SystemProperty property1 = systemPropertyService.findById(id1);
        SystemProperty property2 = systemPropertyService.findById(id2);
        assertTrue(properties.contains(property1));
        assertTrue(properties.contains(property2));
    }

    @Test
    public void findById_When_PropertyNotExist_Expect_EmptyResult() {
        int idIsNotExist = 10;
        assertNull(systemPropertyService.findById(idIsNotExist));
    }

    @Test
    public void update_When_Property_Expect_Update() {
        List<SystemProperty> properties = systemPropertyService.findAll();
        String newName = "StartDocumentNew";
        SystemProperty systemProperty = properties.get(0);
        assertNotNull(systemProperty);
        systemProperty.setName(newName);
        assertEquals(systemProperty, properties.get(0));
    }

    @Test
    public void deleteById_When_PropertyExist_Expect_CorrectDeleteById() {
        int id = 2;
        assertNotNull(systemPropertyService.findById(id));
        systemPropertyService.deleteById(id);

    }

    @Test(expected = DataAccessException.class)
    public void deleteById_When_PropertyNotExist_Expect_EmptyResult() {
        int idIsNotExist = 10;
        systemPropertyService.deleteById(idIsNotExist);
    }

}