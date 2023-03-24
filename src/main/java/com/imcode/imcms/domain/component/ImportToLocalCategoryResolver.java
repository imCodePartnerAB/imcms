package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.dto.ImportCategoryDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportToLocalCategoryResolver {
	private final CategoryService categoryService;
	private final ImportToLocalCategoryTypeResolver categoryTypeResolver;
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	public Category resolve(ImportCategoryDTO importCategory) {
		final String name = importCategory.getName();
		final ImportEntityReferenceDTO categoryReference = importEntityReferenceManagerService.getReference(name, ImportEntityReferenceType.CATEGORY);
		final Integer categoryReferenceLinkedEntityId = categoryReference.getLinkedEntityId();

		CategoryDTO category;
		if (categoryReferenceLinkedEntityId != null) {
			category = categoryService.getById(categoryReferenceLinkedEntityId)
					.map(CategoryDTO::new)
					.orElse(null);
		} else if (!categoryService.existsByName(name)) {
			category = new CategoryDTO();
			category.setName(name);
			category.setDescription(importCategory.getDescription());
			category.setType(categoryTypeResolver.resolve(importCategory.getCategoryType()));

			category = new CategoryDTO(categoryService.save(category));
		} else {
			category = categoryService.getByName(name).map(CategoryDTO::new).orElse(null);
		}

		return category;
	}
}
