package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.DocumentsExistsException;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.CategoryTypeDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @see CategoryService
 */
@Transactional
public class CategoryServiceTest extends WebAppSpringTestConfig {

    private static int COUNT_DATA = 4;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private CategoryTypeDataInitializer categoryTypeDataInitializer;

    @BeforeEach
    public void cleanData() {
        categoryDataInitializer.cleanRepositories();
        categoryTypeDataInitializer.cleanRepositories();
    }

    @Test
    public void getAll_When_CategoriesExists_Expected_CorrectEntities() {
        List<CategoryJPA> categories = categoryDataInitializer.createData(COUNT_DATA);
        assertEquals(categories.size(), categoryService.getAll().size());
    }

    @Test
    public void getById_When_CategoryExist_Expect_CorrectEntity() {
        categoryDataInitializer.createData(COUNT_DATA);
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);

        assertNotNull(firstCategoryDTO);

        final Integer id = firstCategoryDTO.getId();
        assertTrue(categoryService.getById(id).isPresent());
    }

    @Test
    public void save_When_CategoryNotExistBefore_Expect_Saved() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(1);
        assertNotNull(typesData);
        final CategoryTypeJPA firstCategoryType = new CategoryTypeJPA(typesData.get(0));
        final Category category = new CategoryJPA("name", "dummy", "/..", firstCategoryType);
        final Category savedCategory = categoryService.save(category);

        assertTrue(categoryService.getAll().contains(savedCategory));
        assertNotNull(categoryService.getById(savedCategory.getId()));
    }

    @Test
    public void save_When_CategoryNameExist_Expect_CorrectException() {
        final List<CategoryJPA> categories = categoryDataInitializer.createData(COUNT_DATA);
        final Category firstCategory = categories.get(0);
        final CategoryTypeJPA firstCategoryType = categories.get(0).getType();
        final Category category = new CategoryJPA(firstCategory.getName(), "dummy", "/..", firstCategoryType);

        assertThrows(DataIntegrityViolationException.class, () -> categoryService.save(category));
    }

    @Test
    public void update_When_CategoryExists_Expected_UpdatedCorrectEntity() {
        categoryDataInitializer.createData(COUNT_DATA);
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final String otherName = "Other_category_name";
        firstCategoryDTO.setName(otherName);

        final Category savedUpdateCategory = categoryService.update(firstCategoryDTO);

        assertTrue(categoryService.getAll().contains(savedUpdateCategory));

        assertNotNull(savedUpdateCategory);
        assertEquals(firstCategoryDTO.getId(), savedUpdateCategory.getId());
    }

    @Test
    public void update_When_CategoryNameNotUniqueInCategoryType_Expected_CorrectException() {
        categoryDataInitializer.createData(COUNT_DATA);
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);

        final String existName = categoriesDTO.get(1).getName();
        final CategoryType categoryType = categoriesDTO.get(1).getType();
        firstCategoryDTO.setName(existName);
        firstCategoryDTO.setType(categoryType);
        assertEquals(existName, firstCategoryDTO.getName());

        assertThrows(DataIntegrityViolationException.class, () -> categoryService.update(firstCategoryDTO));
    }

    @Test
    public void update_When_CategoryNameExistButNotCurrentCategoryType_Expected_Updated() {
        categoryDataInitializer.createData(COUNT_DATA);
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);

        final String existName = categoriesDTO.get(1).getName();
        firstCategoryDTO.setName(existName);
        assertEquals(existName, firstCategoryDTO.getName());

        assertNotNull(categoryService.update(firstCategoryDTO));
    }

    @Test
    public void update_When_CategoryNameIsEmpty_Expected_CorrectEntity() {
        categoryDataInitializer.createData(COUNT_DATA);
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);

        firstCategoryDTO.setName("");

        assertNotNull(categoryService.update(firstCategoryDTO));
    }

    @Test
    public void delete_When_CategoryNotAssignedToAnyDocument_Expect_Deleted() {
        categoryDataInitializer.createData(COUNT_DATA);
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final Integer firstCategoryId = firstCategoryDTO.getId();

        assertTrue(categoryService.getById(firstCategoryId).isPresent());

        categoryService.delete(firstCategoryId);

        assertFalse(categoryService.getById(firstCategoryId).isPresent());
    }

    @Test
    public void delete_When_DocumentsUsingCategory_Expect_CorrectException() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
        categoryDataInitializer.createData(COUNT_DATA);

        final DocumentDTO doc = documentDataInitializer.createData();

        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        assertNotNull(doc);

        doc.setCategories(new HashSet<>(Collections.singleton(firstCategoryDTO)));
        final DocumentDTO savedDoc = documentService.save(doc);
        assertNotNull(savedDoc);
        final Integer docId = savedDoc.getId();
        final Integer categoryId = firstCategoryDTO.getId();
        final List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(categoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.isEmpty());
        assertTrue(categoryDocIds.contains(docId));

        assertThrows(DocumentsExistsException.class, () -> categoryService.delete(categoryId));
    }
}
