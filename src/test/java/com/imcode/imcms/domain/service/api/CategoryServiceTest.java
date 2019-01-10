package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @see CategoryService
 */
@Transactional
public class CategoryServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private DocumentService<TextDocumentDTO> textDocumentService;

    @Autowired
    private DocumentService<UrlDocumentDTO> urlDocumentService;

    @Autowired
    private DocumentService<FileDocumentDTO> fileDocumentService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private DocumentDtoFactory documentDtoFactory;

    @BeforeEach
    public void setUp() {
        categoryDataInitializer.createData(4);
    }

    @AfterEach
    public void cleanUp() {
        //categoryDataInitializer.cleanRepositories(); todo: really need this ?
    }

    @Test
    public void getAll_Expected_CorrectEntities() {
        List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        assertEquals(categoriesDTO.size(), categoryService.getAll().size());
    }

    @Test
    public void getById_When_CategoryExist_Expect_CorrectEntity() {
        List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        CategoryDTO firstCategoryDTO = categoriesDTO.get(0);

        assertNotNull(firstCategoryDTO);

        final Integer id = firstCategoryDTO.getId();
        assertTrue(categoryService.getById(id).isPresent());
    }

    @Test
    public void save_When_CategoryNotExistBefore_Expect_Saved() {
        final List<CategoryTypeDTO> categoryTypesDTO = categoryDataInitializer.getCategoryTypesAsDTO();
        final CategoryTypeJPA firstCategoryType = new CategoryTypeJPA(categoryTypesDTO.get(0));
        final Category category = new CategoryJPA("name", "dummy", "", firstCategoryType);
        final Category savedCategory = categoryService.save(category);

        assertTrue(categoryService.getAll().contains(savedCategory));
    }

    @Test
    public void save_When_CategoryNameExist_Expect_CorrectException() {
        final List<CategoryTypeDTO> categoryTypesDTO = categoryDataInitializer.getCategoryTypesAsDTO();
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final CategoryTypeJPA firstCategoryType = new CategoryTypeJPA(categoryTypesDTO.get(0));
        final Category category = new CategoryJPA(firstCategoryDTO.getName(), "dummy", "", firstCategoryType);

        assertThrows(IllegalArgumentException.class, () -> categoryService.save(category));
    }

    @Test
    public void update_When_GetExistCategory_Expected_UpdatedCorrectEntity() {
        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final String otherName = "Other_category_name";
        firstCategoryDTO.setName(otherName);

        Category savedUpdateCategory = categoryService.update(firstCategoryDTO);

        assertTrue(categoryService.getAll().contains(savedUpdateCategory));

        assertNotNull(savedUpdateCategory);
        assertEquals(firstCategoryDTO.getId(), savedUpdateCategory.getId());
    }

    @Test
    public void update_When_CategoryNameExist_Expected_CorrectException() {
        List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        CategoryDTO firstCategoryDTO = categoriesDTO.get(0);

        final String existName = categoriesDTO.get(1).getName();
        firstCategoryDTO.setName(existName);

        assertThrows(IllegalArgumentException.class, () -> categoryService.update(firstCategoryDTO));
    }

    @Test
    public void update_When_CategoryNameIsEmpty_Expected_CorrectException() {
        List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        CategoryDTO firstCategoryDTO = categoriesDTO.get(0);

        firstCategoryDTO.setName("");

        assertThrows(RuntimeException.class, () -> categoryService.update(firstCategoryDTO));
    }

    @Test
    public void delete_When_DocumentHasNotCategory_Expect_Deleted() {
        List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final Integer firstCategoryId = firstCategoryDTO.getId();

        assertTrue(categoryService.getById(firstCategoryId).isPresent());

        categoryService.delete(firstCategoryId);

        assertFalse(categoryService.getById(firstCategoryId).isPresent());
    }

    @Test
    public void delete_When_CategoryHasTextDocument_Expect_CorrectException() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final TextDocumentDTO textDocument = documentDtoFactory.createEmptyTextDocument();
        assertNotNull(textDocument);

        textDocument.setCategories(new HashSet<>(Collections.singleton(firstCategoryDTO)));
        final TextDocumentDTO savedTextDocument = textDocumentService.save(textDocument);

        final Integer docId = savedTextDocument.getId();
        final Integer categoryId = firstCategoryDTO.getId();

        final List<Meta> allMetas = metaRepository.findAll();

        final List<Integer> metaIds = allMetas.stream().map(Meta::getId).collect(Collectors.toList());
        assertTrue(metaIds.contains(savedTextDocument.getId()));

        final List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(categoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.isEmpty());
        assertTrue(categoryDocIds.contains(docId));

        assertThrows(UnsupportedOperationException.class, () -> categoryService.delete(categoryId));
    }

    @Test
    public void delete_When_CategoryHasUrlDocument_Expect_CorrectException() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final UrlDocumentDTO urlDocument = documentDtoFactory.createEmptyUrlDocument();
        assertNotNull(urlDocument);

        urlDocument.setCategories(new HashSet<>(Collections.singleton(firstCategoryDTO)));
        final UrlDocumentDTO savedUrlDocument = urlDocumentService.save(urlDocument);

        final Integer docId = savedUrlDocument.getId();
        final Integer categoryId = firstCategoryDTO.getId();

        final List<Meta> allMetas = metaRepository.findAll();

        final List<Integer> metaIds = allMetas.stream().map(Meta::getId).collect(Collectors.toList());
        assertTrue(metaIds.contains(savedUrlDocument.getId()));

        final List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(categoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.isEmpty());
        assertTrue(categoryDocIds.contains(docId));

        assertThrows(UnsupportedOperationException.class, () -> categoryService.delete(categoryId));
    }

    @Test
    public void delete_When_CategoryHasFileDocument_Expect_CorrectException() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        final List<CategoryDTO> categoriesDTO = categoryDataInitializer.getCategoriesAsDTO();
        final CategoryDTO firstCategoryDTO = categoriesDTO.get(0);
        final FileDocumentDTO fileDocument = documentDtoFactory.createEmptyFileDocument();
        assertNotNull(fileDocument);

        fileDocument.setCategories(new HashSet<>(Collections.singleton(firstCategoryDTO)));
        final FileDocumentDTO savedFileDocument = fileDocumentService.save(fileDocument);

        final Integer docId = savedFileDocument.getId();
        final Integer categoryId = firstCategoryDTO.getId();

        final List<Meta> allMetas = metaRepository.findAll();

        final List<Integer> metaIds = allMetas.stream().map(Meta::getId).collect(Collectors.toList());
        assertTrue(metaIds.contains(savedFileDocument.getId()));

        final List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(categoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.isEmpty());
        assertTrue(categoryDocIds.contains(docId));

        assertThrows(UnsupportedOperationException.class, () -> categoryService.delete(categoryId));
    }
}
