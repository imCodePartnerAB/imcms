package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.dto.ImportCategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportToLocalCategoryTypeResolver {
	private final CategoryTypeService categoryTypeService;
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	public CategoryTypeDTO resolve(ImportCategoryTypeDTO importCategoryType) {
		final String name = importCategoryType.getName();
		final ImportEntityReferenceDTO categoryTypeReference = importEntityReferenceManagerService.getReference(name, ImportEntityReferenceType.CATEGORY_TYPE);
		final Integer categoryTypeReferenceId = categoryTypeReference.getLinkedEntityId();

		CategoryTypeDTO categoryType;
		if (categoryTypeReferenceId != null) {
			categoryType = categoryTypeService.get(categoryTypeReferenceId).map(CategoryTypeDTO::new).orElse(null);
		} else if (!categoryTypeService.existsByName(name)) {
			categoryType = new CategoryTypeDTO();
			categoryType.setName(name);
			categoryType.setInherited(importCategoryType.isInherited());
			categoryType.setMultiSelect(importCategoryType.isMultiselect());

			categoryType = new CategoryTypeDTO(categoryTypeService.create(categoryType));
		} else {
			categoryType = categoryTypeService.getByName(name).map(CategoryTypeDTO::new).orElse(null);
		}

		return categoryType;
	}
}
