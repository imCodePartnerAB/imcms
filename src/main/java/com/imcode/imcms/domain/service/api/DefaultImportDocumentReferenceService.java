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

import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class DefaultImportDocumentReferenceService implements ImportDocumentReferenceService {
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	@Override
	public void createReferences(ImportDocumentDTO importDocument) {
		final BiConsumer<String, ImportEntityReferenceType> createReferenceIfNotBlank = (name, type) -> {
			if (StringUtils.isNotBlank(name)) {
				importEntityReferenceManagerService.createReference(name, type);
			}
		};

		for (ImportCategoryDTO category : importDocument.getCategories()) {
			createReferenceIfNotBlank.accept(category.getName(), ImportEntityReferenceType.CATEGORY);
			createReferenceIfNotBlank.accept(category.getCategoryType().getName(), ImportEntityReferenceType.CATEGORY_TYPE);
		}

		for (ImportRoleDTO importRole : importDocument.getRoles()) {
			createReferenceIfNotBlank.accept(importRole.getName(), ImportEntityReferenceType.ROLE);
		}

		createReferenceIfNotBlank.accept(importDocument.getTemplate(), ImportEntityReferenceType.TEMPLATE);
	}
}
