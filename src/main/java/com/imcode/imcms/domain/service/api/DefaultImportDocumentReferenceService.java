package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImportCategoryDTO;
import com.imcode.imcms.domain.dto.ImportDocumentDTO;
import com.imcode.imcms.domain.dto.ImportRoleDTO;
import com.imcode.imcms.domain.service.ImportDocumentReferenceService;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultImportDocumentReferenceService implements ImportDocumentReferenceService {
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	@Override
	public void createReferences(ImportDocumentDTO importDocument) {
		for (ImportCategoryDTO category : importDocument.getCategories()) {
			createReferenceIfNotBlank(category.getName(), ImportEntityReferenceType.CATEGORY);
			createReferenceIfNotBlank(category.getCategoryType().getName(), ImportEntityReferenceType.CATEGORY_TYPE);
		}

		for (ImportRoleDTO importRole : importDocument.getRoles()) {
			createReferenceIfNotBlank(importRole.getName(), ImportEntityReferenceType.ROLE);
		}

		createReferenceIfNotBlank(importDocument.getTemplate(), ImportEntityReferenceType.TEMPLATE);
	}

	private void createReferenceIfNotBlank(String name, ImportEntityReferenceType type) {
		if (StringUtils.isNotBlank(name)) {
			importEntityReferenceManagerService.createReference(name, type);
		}
	}
}
