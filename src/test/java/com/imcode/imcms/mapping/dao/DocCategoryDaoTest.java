package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocCategory;
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
public class DocCategoryDaoTest {

    @Inject
    DocCategoryTypeDao docCategoryTypeDao;

    @Inject
    DocCategoryDao docCategoryDao;

    List<DocCategoryType> types;
    List<DocCategory> categories;

    @Before
    public void setUp() {
        types = recreateTypes();
        categories = recreateCategories();
    }

    public List<DocCategoryType> recreateTypes() {
        return Arrays.asList(
                docCategoryTypeDao.saveAndFlush(new DocCategoryType("DocCategoryTypeOne", 0, false, false)),
                docCategoryTypeDao.saveAndFlush(new DocCategoryType("DocCategoryTypeTwo", 0, false, false))
        );
    }

    public List<DocCategory> recreateCategories() {
        return Arrays.asList(
                docCategoryDao.saveAndFlush(
                        new DocCategory(
                                "Group1", "Group1Description", "Group1ImageUrl", types.get(0)
                        )
                ),
                docCategoryDao.saveAndFlush(
                        new DocCategory(
                                "Group2", "Group2Description", "Group2ImageUrl", types.get(1)
                        )
                )
        );
    }


    @Test
    public void testFindByType() throws Exception {
        List<DocCategory> docCategoryList = docCategoryDao.findByType(types.get(1));

        assertThat(docCategoryList.size(), is(1));
        assertThat(docCategoryList.get(0).getName(), is("Group2"));
    }

    @Test
    public void testFindByNameAndType() throws Exception {
        DocCategory docCategory1 = docCategoryDao.findByNameAndType("Group1", types.get(1));
        DocCategory docCategory2 = docCategoryDao.findByNameAndType("Group2", types.get(1));

        assertNull(docCategory1);
        assertNotNull(docCategory2);

        assertThat(docCategory2.getName(), is("Group2"));
    }

    @Test
    public void testFindCategoryDocIds() throws Exception {
        fail();
    }

    @Test
    public void testDeleteByDocIdAndCategoryId() throws Exception {
        fail();
    }
}
