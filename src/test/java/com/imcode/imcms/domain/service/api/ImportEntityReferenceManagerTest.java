package com.imcode.imcms.domain.service.api;


import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.CategoryTypeDataInitializer;
import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TextDocumentTemplate;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.repository.ImportCategoryReferenceRepository;
import com.imcode.imcms.persistence.repository.ImportCategoryTypeReferenceRepository;
import com.imcode.imcms.persistence.repository.ImportRoleReferenceRepository;
import com.imcode.imcms.persistence.repository.ImportTemplateReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class ImportEntityReferenceManagerTest extends WebAppSpringTestConfig {
	@Autowired
	private ImportCategoryReferenceRepository importCategoryReferenceRepository;
	@Autowired
	private ImportCategoryTypeReferenceRepository importCategoryTypeReferenceRepository;
	@Autowired
	private ImportRoleReferenceRepository importRoleReferenceRepository;
	@Autowired
	private ImportTemplateReferenceRepository importTemplateReferenceRepository;
	@Autowired
	private ImportEntityReferenceManagerService importEntityReferenceManagerService;
	@Autowired
	private CategoryDataInitializer categoryDataInitializer;
	@Autowired
	private CategoryTypeDataInitializer categoryTypeDataInitializer;
	@Autowired
	private RoleService roleService;
	@Autowired
	private TemplateService templateService;

	@BeforeEach
	public void cleanUp() {
		categoryDataInitializer.cleanRepositories();
		categoryTypeDataInitializer.cleanRepositories();
		importCategoryTypeReferenceRepository.deleteAll();
		importCategoryReferenceRepository.deleteAll();
		importTemplateReferenceRepository.deleteAll();
		importRoleReferenceRepository.deleteAll();

		importCategoryTypeReferenceRepository.flush();
		importCategoryReferenceRepository.flush();
		importTemplateReferenceRepository.flush();
		importRoleReferenceRepository.flush();
	}

	@Test
	public void createEachReferenceType_Expect_CreatedReferences() {
		assertNotNull(importEntityReferenceManagerService.createReference("category_reference", ImportEntityReferenceType.CATEGORY));
		assertNotNull(importEntityReferenceManagerService.createReference("category_type_reference", ImportEntityReferenceType.CATEGORY_TYPE));
		assertNotNull(importEntityReferenceManagerService.createReference("role_reference", ImportEntityReferenceType.ROLE));
		assertNotNull(importEntityReferenceManagerService.createReference("template_reference", ImportEntityReferenceType.TEMPLATE));

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
	public void getAllTypedEntityReferenceForEachType_Expect_CorrectResults() {
		importEntityReferenceManagerService.createReference("category_reference", ImportEntityReferenceType.CATEGORY);
		importEntityReferenceManagerService.createReference("category_type_reference", ImportEntityReferenceType.CATEGORY_TYPE);
		importEntityReferenceManagerService.createReference("role_reference", ImportEntityReferenceType.ROLE);
		importEntityReferenceManagerService.createReference("template_reference", ImportEntityReferenceType.TEMPLATE);

		assertEquals(1, importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.CATEGORY).size());
		assertEquals(1, importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.CATEGORY_TYPE).size());
		assertEquals(1, importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.ROLE).size());
		assertEquals(1, importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.TEMPLATE).size());
	}

	@Test
	public void updateEachReferenceTypeWithLinkedEntityId_Expect_CorrectResults() {
		final ImportEntityReferenceDTO categoryReference = importEntityReferenceManagerService.createReference("category_reference", ImportEntityReferenceType.CATEGORY);
		final ImportEntityReferenceDTO categoryTypeReference = importEntityReferenceManagerService.createReference("category_type_reference", ImportEntityReferenceType.CATEGORY_TYPE);
		final ImportEntityReferenceDTO roleReference = importEntityReferenceManagerService.createReference("role_reference", ImportEntityReferenceType.ROLE);
		final ImportEntityReferenceDTO templateReference = importEntityReferenceManagerService.createReference("template_reference", ImportEntityReferenceType.TEMPLATE);

		final CategoryJPA categoryJPA = categoryDataInitializer.createData(1).get(0);
		final Role role = roleService.getByName("Users");
		final Template template = templateService.get("demo");

		categoryReference.setLinkedEntityId(categoryJPA.getId());
		importEntityReferenceManagerService.updateReference(categoryReference);

		categoryTypeReference.setLinkedEntityId(categoryJPA.getType().getId());
		importEntityReferenceManagerService.updateReference(categoryTypeReference);

		roleReference.setLinkedEntityId(role.getId());
		importEntityReferenceManagerService.updateReference(roleReference);

		templateReference.setLinkedEntityId(template.getId());
		importEntityReferenceManagerService.updateReference(templateReference);

		final ImportEntityReferenceDTO updatedCategoryReference = importEntityReferenceManagerService.getReference(categoryReference.getName(), categoryReference.getType());
		assertEquals(categoryJPA.getId(), updatedCategoryReference.getLinkedEntityId());

		final ImportEntityReferenceDTO updatedCategoryTypeReference = importEntityReferenceManagerService.getReference(categoryTypeReference.getName(), categoryTypeReference.getType());
		assertEquals(categoryJPA.getType().getId(), updatedCategoryTypeReference.getLinkedEntityId());

		final ImportEntityReferenceDTO updatedRoleReference = importEntityReferenceManagerService.getReference(roleReference.getName(), roleReference.getType());
		assertEquals(role.getId(), updatedRoleReference.getLinkedEntityId());

		final ImportEntityReferenceDTO updatedTemplateReference = importEntityReferenceManagerService.getReference(templateReference.getName(), templateReference.getType());
		assertEquals(template.getId(), updatedTemplateReference.getLinkedEntityId());
	}

	@Test
	public void updateEachEntityReferenceTypeName_Expect_CorrectExceptions() {
		final ImportEntityReferenceDTO categoryReference = importEntityReferenceManagerService.createReference("category_reference", ImportEntityReferenceType.CATEGORY);
		final ImportEntityReferenceDTO categoryTypeReference = importEntityReferenceManagerService.createReference("category_type_reference", ImportEntityReferenceType.CATEGORY_TYPE);
		final ImportEntityReferenceDTO roleReference = importEntityReferenceManagerService.createReference("role_reference", ImportEntityReferenceType.ROLE);
		final ImportEntityReferenceDTO templateReference = importEntityReferenceManagerService.createReference("template_reference", ImportEntityReferenceType.TEMPLATE);

		categoryReference.setName("name");
		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.updateReference(categoryReference));

		categoryTypeReference.setName("name");
		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.updateReference(categoryTypeReference));

		roleReference.setName("name");
		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.updateReference(roleReference));

		templateReference.setName("name");
		assertThrows(RuntimeException.class, () -> importEntityReferenceManagerService.updateReference(templateReference));
	}

}
