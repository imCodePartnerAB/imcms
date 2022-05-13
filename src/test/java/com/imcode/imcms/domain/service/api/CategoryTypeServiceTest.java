package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.CategoryTypeHasCategoryException;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.CategoryTypeDataInitializer;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CategoryTypeServiceTest extends WebAppSpringTestConfig {

    private static int COUNT_DATA = 3;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private CategoryTypeDataInitializer categoryTypeDataInitializer;
    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @BeforeEach
    public void cleanUp() {
        categoryDataInitializer.cleanRepositories();
        categoryTypeDataInitializer.cleanRepositories();
    }

    @Test
    public void get_When_CategoryTypeExists_Expect_Found() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final List<CategoryTypeDTO> categoryTypesAsDTO = categoryTypeDataInitializer.getCategoryTypesAsDTO();
        final CategoryType firstCategoryType = categoryTypesAsDTO.get(0);
        final Optional<CategoryType> foundType = categoryTypeService.get(firstCategoryType.getId());

        assertTrue(foundType.isPresent());

        assertEquals(foundType.get(), firstCategoryType);
    }

    @Test
    public void getAll_WhenCategoryTypeExists_Expected_CorrectEntities() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        assertNotNull(categoryTypeService.getAll());
        assertEquals(typesData.size(), categoryTypeService.getAll().size());
    }

    @Test
    public void create_When_CategoryTypeNotExists_Expected_Saved() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final CategoryTypeDTO categoryType = new CategoryTypeDTO(new CategoryTypeJPA(
                null, "name", false, true, false
        ));
        assertNotNull(categoryTypeService.create(categoryType));
        assertEquals(COUNT_DATA + 1, categoryTypeService.getAll().size());
    }

    @Test
    public void create_When_CategoryTypeNameAlreadyExists_Expected_CorrectException() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());

        final CategoryType categoryType = new CategoryTypeJPA(null,
                typesData.get(0).getName(), false, true, false
        );
        assertThrows(DataIntegrityViolationException.class, () -> categoryTypeService.create(categoryType));
    }

    @Test
    public void create_When_CategoryTypeNameEmpty_Expected_CorrectException() {
        final CategoryType categoryType = new CategoryTypeJPA(
                null, "", false, true, false
        );
        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.create(categoryType));
    }

    @Test
    public void update_When_CategoryTypeExists_Expected_UpdateEntity() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final CategoryType firstCategoryType = typesData.get(0);
        firstCategoryType.setName("Other Test Name");

        final CategoryType updated = categoryTypeService.update(firstCategoryType);

        assertNotNull(updated);
        assertEquals(firstCategoryType.getId(), updated.getId());
    }

    @Test
    public void update_When_CategoryTypeNameDuplicated_Expected_CorrectException() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final CategoryType firstCategoryType = typesData.get(0);

        firstCategoryType.setName(typesData.get(1).getName());

        assertEquals(firstCategoryType.getName(), typesData.get(1).getName());
        assertThrows(DataIntegrityViolationException.class, () -> categoryTypeService.update(firstCategoryType));
    }

    @Test
    public void delete_When_CategoryTypeHasNotCategories_Expected_Deleted() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final int firstCategoryTypeId = typesData.get(0).getId();
        Optional<CategoryType> foundType = categoryTypeService.get(firstCategoryTypeId);

        assertTrue(foundType.isPresent());

        categoryTypeService.delete(foundType.get().getId());

        foundType = categoryTypeService.get(firstCategoryTypeId);

        assertFalse(foundType.isPresent());
    }

    @Test
    public void delete_When_CategoriesExists_Expected_CorrectException() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final List<CategoryTypeDTO> categoryTypesAsDTO = categoryTypeDataInitializer.getCategoryTypesAsDTO();
        assertEquals(typesData.size(), categoryTypesAsDTO.size());

        final CategoryTypeDTO firstCategoryType = categoryTypesAsDTO.get(0);
        createCategory(firstCategoryType);
        assertThrows(CategoryTypeHasCategoryException.class, () -> categoryTypeService.delete(firstCategoryType.getId()));
    }

    @Test
    public void deleteForce_When_CategoriesExists_Expected_DeletedCategoriesInDocumentAndCategoriesAndCategoryType() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final List<CategoryTypeDTO> categoryTypesAsDTO = categoryTypeDataInitializer.getCategoryTypesAsDTO();
        assertEquals(typesData.size(), categoryTypesAsDTO.size());

        final CategoryTypeDTO firstCategoryType = categoryTypesAsDTO.get(0);
        final Category category = createCategory(firstCategoryType);
        final int categoryTypeId = firstCategoryType.getId();
        final int categoryId = category.getId();

        final DocumentDTO doc = documentService.get(1001);
        assertTrue(doc.getCategories().isEmpty());
        doc.setCategories(new HashSet<>(Collections.singleton(category)));
        final DocumentDTO savedDoc = documentService.save(doc);
        assertNotNull(savedDoc);
        assertFalse(savedDoc.getCategories().isEmpty());

        categoryTypeService.deleteForce(categoryTypeId);

	    assertTrue(categoryRepository.findById(categoryId).isEmpty());
	    assertTrue(documentService.get(savedDoc.getId()).getCategories().isEmpty());
        assertFalse(categoryTypeService.get(categoryId).isPresent());
    }

    private Category createCategory(CategoryTypeDTO categoryTypeDTO) {
        return categoryRepository.save(
                new CategoryJPA(null, "name", "description", modelMapper.map(categoryTypeDTO, CategoryTypeJPA.class))
        );
    }
}
