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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
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

        final Optional<CategoryType> oFound = categoryTypeService.get(saved.getId());

        assertTrue(oFound.isPresent());

        final CategoryType found = oFound.get();

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

        final Optional<CategoryType> oFound = categoryTypeService.get(saved.getId());

        assertTrue(oFound.isPresent());

        final CategoryType found = oFound.get();

        assertEquals(found, saved);
    }

    @Test
    public void delete_When_Exist_Expect_Deleted() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false, new ArrayList<>()
        );
        final CategoryType saved = categoryTypeService.save(categoryType);

        final Integer savedId = saved.getId();
        Optional<CategoryType> oFound = categoryTypeService.get(savedId);

        assertTrue(oFound.isPresent());

        categoryTypeService.delete(savedId);

        oFound = categoryTypeService.get(savedId);

        assertFalse(oFound.isPresent());
    }
}
