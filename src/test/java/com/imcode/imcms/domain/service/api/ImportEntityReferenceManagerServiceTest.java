package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.ImportEntityReferenceDataInitializer;
import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImportEntityReferenceManagerServiceTest extends WebAppSpringTestConfig {
	@Autowired
	private ImportEntityReferenceManagerService importEntityReferenceManagerService;
	@Autowired
	private ImportEntityReferenceDataInitializer importEntityReferenceDataInitializer;
	@Autowired
	private CategoryDataInitializer categoryDataInitializer;
	@Autowired
	private RoleService roleService;
	@Autowired
	private TemplateService templateService;

	private final String CATEGORY_REFERENCE_NAME = "categoryReference";
	private final String CATEGORY_TYPE_REFERENCE_NAME = "categoryTypeReference";
	private final String ROLE_REFERENCE_NAME = "roleReference";
	private final String TEMPLATE_REFERENCE_NAME = "templateReference";

	@Test
	public void createImportReferences_Expect_Created() {
		assertNotNull(importEntityReferenceManagerService.createReference(CATEGORY_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY));
		assertNotNull(importEntityReferenceManagerService.createReference(CATEGORY_TYPE_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY_TYPE));
		assertNotNull(importEntityReferenceManagerService.createReference(ROLE_REFERENCE_NAME, ImportEntityReferenceType.ROLE));
		assertNotNull(importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME, ImportEntityReferenceType.TEMPLATE));

		assertNotNull(importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO("ctg_ref_dto", ImportEntityReferenceType.CATEGORY)));
		assertNotNull(importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO("ctg_type_ref_dto", ImportEntityReferenceType.CATEGORY_TYPE)));
		assertNotNull(importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO("role_ref_dto", ImportEntityReferenceType.ROLE)));
		assertNotNull(importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO("template_ref_dto", ImportEntityReferenceType.TEMPLATE)));
	}

	@Test
	public void createNotNewEachReferenceType_Expect_Exception() {
		final String referenceName = "reference";

		assertNotNull(importEntityReferenceManagerService.createReference(referenceName, ImportEntityReferenceType.CATEGORY));
		assertNotNull(importEntityReferenceManagerService.createReference(referenceName, ImportEntityReferenceType.CATEGORY_TYPE));
		assertNotNull(importEntityReferenceManagerService.createReference(referenceName, ImportEntityReferenceType.ROLE));
		assertNotNull(importEntityReferenceManagerService.createReference(referenceName, ImportEntityReferenceType.TEMPLATE));

		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO(referenceName, ImportEntityReferenceType.CATEGORY)));
		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO(referenceName, ImportEntityReferenceType.CATEGORY_TYPE)));
		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO(referenceName, ImportEntityReferenceType.ROLE)));
		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.createReference(new ImportEntityReferenceDTO(referenceName, ImportEntityReferenceType.TEMPLATE)));
	}

	@Test
	public void createImportReferences_And_GetThemOneByOne_Expect_CorrectResults() {
		importEntityReferenceManagerService.createReference(CATEGORY_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY);
		importEntityReferenceManagerService.createReference(CATEGORY_TYPE_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY_TYPE);
		importEntityReferenceManagerService.createReference(ROLE_REFERENCE_NAME, ImportEntityReferenceType.ROLE);
		importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME, ImportEntityReferenceType.TEMPLATE);

		assertNotNull(importEntityReferenceManagerService.getReference(CATEGORY_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY));
		assertNotNull(importEntityReferenceManagerService.getReference(CATEGORY_TYPE_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY_TYPE));
		assertNotNull(importEntityReferenceManagerService.getReference(ROLE_REFERENCE_NAME, ImportEntityReferenceType.ROLE));
		assertNotNull(importEntityReferenceManagerService.getReference(TEMPLATE_REFERENCE_NAME, ImportEntityReferenceType.TEMPLATE));
	}

	@Test
	public void createImportReferences_And_GetThemAllByType_Expect_CorrectResults() {
		importEntityReferenceManagerService.createReference(CATEGORY_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY);

		importEntityReferenceManagerService.createReference(CATEGORY_TYPE_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY_TYPE);
		importEntityReferenceManagerService.createReference(CATEGORY_TYPE_REFERENCE_NAME + 1, ImportEntityReferenceType.CATEGORY_TYPE);

		importEntityReferenceManagerService.createReference(ROLE_REFERENCE_NAME, ImportEntityReferenceType.ROLE);
		importEntityReferenceManagerService.createReference(ROLE_REFERENCE_NAME + 1, ImportEntityReferenceType.ROLE);
		importEntityReferenceManagerService.createReference(ROLE_REFERENCE_NAME + 2, ImportEntityReferenceType.ROLE);

		importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME, ImportEntityReferenceType.TEMPLATE);
		importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME + 1, ImportEntityReferenceType.TEMPLATE);
		importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME + 2, ImportEntityReferenceType.TEMPLATE);
		importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME + 3, ImportEntityReferenceType.TEMPLATE);

		final List<ImportEntityReferenceDTO> categoryReferences = importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.CATEGORY);
		final List<ImportEntityReferenceDTO> categoryTypeReferences = importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.CATEGORY_TYPE);
		final List<ImportEntityReferenceDTO> roleReferences = importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.ROLE);
		final List<ImportEntityReferenceDTO> templateReferences = importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.TEMPLATE);

		assertEquals(1, categoryReferences.size());
		assertEquals(2, categoryTypeReferences.size());
		assertEquals(3, roleReferences.size());
		assertEquals(4, templateReferences.size());
	}

	@Test
	public void updateImportReferences_Expect_Updated() {
		final ImportEntityReferenceDTO categoryReference = importEntityReferenceManagerService.createReference(CATEGORY_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY);
		final ImportEntityReferenceDTO categoryTypeReference = importEntityReferenceManagerService.createReference(CATEGORY_TYPE_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY_TYPE);
		final ImportEntityReferenceDTO roleReference = importEntityReferenceManagerService.createReference(ROLE_REFERENCE_NAME, ImportEntityReferenceType.ROLE);
		final ImportEntityReferenceDTO templateReference = importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME, ImportEntityReferenceType.TEMPLATE);

		final CategoryJPA categoryJPA = categoryDataInitializer.createData(1).stream().findFirst().get();
		categoryReference.setLinkedEntityId(categoryJPA.getId());
		importEntityReferenceManagerService.updateReference(categoryReference);

		categoryTypeReference.setLinkedEntityId(categoryJPA.getType().getId());
		importEntityReferenceManagerService.updateReference(categoryTypeReference);

		final Role role = roleService.getAll().stream().findAny().get();
		roleReference.setLinkedEntityId(role.getId());
		importEntityReferenceManagerService.updateReference(roleReference);

		final Template template = templateService.getAll().stream().findAny().get();
		templateReference.setLinkedEntityId(template.getId());
		importEntityReferenceManagerService.updateReference(templateReference);

		final ImportEntityReferenceDTO categoryReferenceTest = importEntityReferenceManagerService.getReference(CATEGORY_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY);
		final ImportEntityReferenceDTO categoryTypeReferenceTest = importEntityReferenceManagerService.getReference(CATEGORY_TYPE_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY_TYPE);
		final ImportEntityReferenceDTO roleReferenceTest = importEntityReferenceManagerService.getReference(ROLE_REFERENCE_NAME, ImportEntityReferenceType.ROLE);
		final ImportEntityReferenceDTO templateReferenceTest = importEntityReferenceManagerService.getReference(TEMPLATE_REFERENCE_NAME, ImportEntityReferenceType.TEMPLATE);

		assertEquals(categoryJPA.getId(), categoryReferenceTest.getLinkedEntityId());
		assertEquals(categoryJPA.getType().getId(), categoryTypeReferenceTest.getLinkedEntityId());
		assertEquals(role.getId(), roleReferenceTest.getLinkedEntityId());
		assertEquals(template.getId(), templateReferenceTest.getLinkedEntityId());
	}

	@Test
	public void createImportReferences_And_CheckIfExists_Expect_CorrectResults() {
		final ImportEntityReferenceDTO categoryReference = importEntityReferenceManagerService.createReference(CATEGORY_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY);
		final ImportEntityReferenceDTO categoryTypeReference = importEntityReferenceManagerService.createReference(CATEGORY_TYPE_REFERENCE_NAME, ImportEntityReferenceType.CATEGORY_TYPE);
		final ImportEntityReferenceDTO roleReference = importEntityReferenceManagerService.createReference(ROLE_REFERENCE_NAME, ImportEntityReferenceType.ROLE);
		final ImportEntityReferenceDTO templateReference = importEntityReferenceManagerService.createReference(TEMPLATE_REFERENCE_NAME, ImportEntityReferenceType.TEMPLATE);

		assertTrue(importEntityReferenceManagerService.existsByName(categoryReference.getName(), categoryReference.getType()));
		assertTrue(importEntityReferenceManagerService.existsByName(categoryTypeReference.getName(), categoryTypeReference.getType()));
		assertTrue(importEntityReferenceManagerService.existsByName(roleReference.getName(), roleReference.getType()));
		assertTrue(importEntityReferenceManagerService.existsByName(templateReference.getName(), templateReference.getType()));
	}

	@BeforeEach
	public void clean() {
		importEntityReferenceDataInitializer.cleanRepositories();
		categoryDataInitializer.cleanRepositories();
	}
}
