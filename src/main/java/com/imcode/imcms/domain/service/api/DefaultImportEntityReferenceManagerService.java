package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.AbstractImportEntityReference;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class DefaultImportEntityReferenceManagerService<T extends AbstractImportEntityReference> implements ImportEntityReferenceManagerService {
	private final ImportRoleReferenceService importRoleReferenceService;
	private final ImportCategoryReferenceService importCategoryReferenceService;
	private final ImportCategoryTypeReferenceService importCategoryTypeReferenceService;
	private final ImportTemplateReferenceService importTemplateReferenceService;

	public DefaultImportEntityReferenceManagerService(ImportRoleReferenceService importRoleReferenceService,
	                                                  ImportCategoryReferenceService importCategoryReferenceService,
	                                                  ImportCategoryTypeReferenceService importCategoryTypeReferenceService,
	                                                  ImportTemplateReferenceService importTemplateReferenceService) {
		this.importRoleReferenceService = importRoleReferenceService;
		this.importCategoryReferenceService = importCategoryReferenceService;
		this.importCategoryTypeReferenceService = importCategoryTypeReferenceService;
		this.importTemplateReferenceService = importTemplateReferenceService;
	}

	@Override
	public ImportEntityReferenceDTO createReference(String name, ImportEntityReferenceType type) {
		return getService(type).create(name, type);
	}

	@Override
	public ImportEntityReferenceDTO createReference(ImportEntityReferenceDTO importReference) {
		return getService(importReference.getType()).create(importReference);
	}

	@Override
	public void updateReference(ImportEntityReferenceDTO reference) {
		getService(reference.getType()).update(reference);
	}

	@Override
	public ImportEntityReferenceDTO getReference(String name, ImportEntityReferenceType type) {
		return getService(type).getByName(name);
	}

	@Override
	public List<ImportEntityReferenceDTO> getAllReferencesByType(ImportEntityReferenceType type) {
		return getService(type).getAll();
	}

	@Override
	public boolean existsByName(String name, ImportEntityReferenceType type) {
		return getService(type).existsByName(name);
	}

	private ImportEntityReferenceService getService(ImportEntityReferenceType type) {
		return switch (type) {
			case CATEGORY_TYPE -> importCategoryTypeReferenceService;
			case CATEGORY -> importCategoryReferenceService;
			case TEMPLATE -> importTemplateReferenceService;
			case ROLE -> importRoleReferenceService;
		};
	}
}
