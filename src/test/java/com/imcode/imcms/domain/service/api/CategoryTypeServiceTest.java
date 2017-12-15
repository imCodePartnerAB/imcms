package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class CategoryTypeServiceTest {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryDataInitializer categoryDataInitilizer;

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Before
    public void setUpCategoryDataInitilizer() {
        categoryDataInitilizer.createData(4);
    }

    @After
    public void clearData() {
        categoryDataInitilizer.cleanRepositories();
    }

    @Test
    public void get_When_Exist_Expect_Found() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false, new ArrayList<>()
        );
        final CategoryType saved = new CategoryTypeDTO(categoryTypeRepository.save(categoryType));

        final CategoryType found = categoryTypeService.get(saved.getId());

        assertEquals(found, saved);
    }

    @Test
    public void getAllExpectedEqualsCategoryTypesAsDtoTest() {
        assertEquals(categoryDataInitilizer.getCategoryTypesAsDTO(), categoryTypeService.getAll());
    }

    @Test
    public void save_When_NotExitBefore_Expect_Saved() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false, new ArrayList<>()
        );
        final CategoryType saved = categoryTypeService.save(categoryType);

        final CategoryType found = categoryTypeService.get(saved.getId());

        assertEquals(found, saved);
    }

}
