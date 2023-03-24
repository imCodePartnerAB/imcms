package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.AbstractImportEntityReferenceServiceImpl;
import com.imcode.imcms.domain.service.ImportCategoryReferenceService;
import com.imcode.imcms.persistence.entity.ImportCategoryReferenceJPA;
import com.imcode.imcms.persistence.repository.ImportCategoryReferenceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class DefaultImportCategoryReferenceService extends AbstractImportEntityReferenceServiceImpl<ImportCategoryReferenceJPA>
		implements ImportCategoryReferenceService {
	private ImportCategoryReferenceRepository importCategoryReferenceRepository;

	public DefaultImportCategoryReferenceService(ImportCategoryReferenceRepository importCategoryReferenceRepository) {
		super(importCategoryReferenceRepository);
		this.importCategoryReferenceRepository = importCategoryReferenceRepository;
	}
}
