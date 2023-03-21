package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.AbstractImportEntityReferenceServiceImpl;
import com.imcode.imcms.domain.service.ImportCategoryTypeReferenceService;
import com.imcode.imcms.persistence.entity.ImportCategoryTypeReferenceJPA;
import com.imcode.imcms.persistence.repository.ImportCategoryTypeReferenceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class DefaultImportCategoryTypeReferenceService extends AbstractImportEntityReferenceServiceImpl<ImportCategoryTypeReferenceJPA>
		implements ImportCategoryTypeReferenceService {
	private final ImportCategoryTypeReferenceRepository importCategoryTypeReferenceRepository;


	protected DefaultImportCategoryTypeReferenceService(ImportCategoryTypeReferenceRepository importCategoryTypeReferenceRepository) {
		super(importCategoryTypeReferenceRepository);
		this.importCategoryTypeReferenceRepository = importCategoryTypeReferenceRepository;
	}
}
