package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocCategoryType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@Transactional
public class DocCategoryTypeDaoTest {


    @Inject
    DocCategoryTypeDao dao;

    List<DocCategoryType> types;

    @Before
    public void setUp() {
        types = recreateTypes();
    }

    public List<DocCategoryType> recreateTypes() {
        return Arrays.asList(
            dao.saveAndFlush(new DocCategoryType("DocCategoryTypeOne", 0, false, false)),
            dao.saveAndFlush(new DocCategoryType("DocCategoryTypeTwo", 0, false, false))
        );
    }

    @Test
    public void testFindByName() throws Exception {
        DocCategoryType type1 = dao.findByName("DocCategoryTypeOne");
        DocCategoryType type2 = dao.findByName("DocCategoryTypeTWO");

        assertNotNull(type1);
        assertNull(type2);
    }

    @Test
    public void testFindByNameIgnoreCase() throws Exception {
        DocCategoryType type1 = dao.findByNameIgnoreCase("DocCategoryTypeOne");
        DocCategoryType type2 = dao.findByNameIgnoreCase("DocCategoryTypeTWO");

        assertNotNull(type1);
        assertNotNull(type2);
    }
}
