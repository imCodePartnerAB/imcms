package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.persistence.repository.ImportCategoryReferenceRepository;
import com.imcode.imcms.persistence.repository.ImportCategoryTypeReferenceRepository;
import com.imcode.imcms.persistence.repository.ImportRoleReferenceRepository;
import com.imcode.imcms.persistence.repository.ImportTemplateReferenceRepository;
import org.springframework.stereotype.Component;

@Component
public class ImportEntityReferenceDataInitializer extends TestDataCleaner {

	private final ImportCategoryReferenceRepository importCategoryReferenceRepository;
	private final ImportCategoryTypeReferenceRepository importCategoryTypeReferenceRepository;
	private final ImportRoleReferenceRepository importRoleReferenceRepository;
	private final ImportTemplateReferenceRepository importTemplateReferenceRepository;

	public ImportEntityReferenceDataInitializer(ImportCategoryReferenceRepository importCategoryReferenceRepository,
	                                            ImportCategoryTypeReferenceRepository importCategoryTypeReferenceRepository,
	                                            ImportRoleReferenceRepository importRoleReferenceRepository,
	                                            ImportTemplateReferenceRepository importTemplateReferenceRepository) {

		super(importCategoryReferenceRepository, importCategoryTypeReferenceRepository, importRoleReferenceRepository, importTemplateReferenceRepository);
		this.importCategoryReferenceRepository = importCategoryReferenceRepository;
		this.importCategoryTypeReferenceRepository = importCategoryTypeReferenceRepository;
		this.importRoleReferenceRepository = importRoleReferenceRepository;
		this.importTemplateReferenceRepository = importTemplateReferenceRepository;
	}


}
