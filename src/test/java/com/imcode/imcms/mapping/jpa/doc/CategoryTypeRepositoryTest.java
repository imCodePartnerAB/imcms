package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CategoryTypeRepositoryTest {

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Before
    public void createCategoriesAndTypes() {
        categoryTypeRepository.saveAndFlush(new CategoryType("DocCategoryTypeOne", 0, false, false));
        categoryTypeRepository.saveAndFlush(new CategoryType("DocCategoryTypeTwo", 0, false, false));
    }

    @Test
    public void testFindByNameIgnoreCase() throws Exception {
        CategoryType type1 = categoryTypeRepository.findByNameIgnoreCase("dOCcATEGORYtYPEoNE");
        CategoryType type2 = categoryTypeRepository.findByNameIgnoreCase("dOCcATEGORYtYPEtWO");

        assertNotNull(type1);
        assertNotNull(type2);
    }

}
