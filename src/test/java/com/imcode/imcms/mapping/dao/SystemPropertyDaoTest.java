package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.SystemProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@Transactional
public class SystemPropertyDaoTest {

    static final String DEFAULT_LANGUAGE_ID = "DefaultLanguageId";
    static final String START_DOC = "startDocument";

    @Inject
    SystemPropertyDao dao;

    @PersistenceContext
    EntityManager entityManager;

    List<SystemProperty> recreateProperties() {
        dao.deleteAll();

        SystemProperty p1 = new SystemProperty(1, START_DOC, "1001");
        SystemProperty p2 = new SystemProperty(8, DEFAULT_LANGUAGE_ID, "1");

        return Arrays.asList(dao.saveAndFlush(p1), dao.saveAndFlush(p2));
    }

    @Test
    public void testFindAll() throws Exception {
        List<SystemProperty> properties = recreateProperties();

        assertEquals(2, dao.findAll().size());
    }

    @Test
    public void testFindByName() throws Exception {
        List<SystemProperty> properties = recreateProperties();

        assertEquals(dao.findByName(START_DOC), properties.get(0));
        assertEquals(dao.findByName(DEFAULT_LANGUAGE_ID), properties.get(1));
    }
}
