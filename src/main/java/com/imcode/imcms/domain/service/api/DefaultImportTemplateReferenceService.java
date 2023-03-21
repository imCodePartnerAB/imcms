package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.AbstractImportEntityReferenceServiceImpl;
import com.imcode.imcms.domain.service.ImportTemplateReferenceService;
import com.imcode.imcms.persistence.entity.ImportTemplateReferenceJPA;
import com.imcode.imcms.persistence.repository.ImportTemplateReferenceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class DefaultImportTemplateReferenceService extends AbstractImportEntityReferenceServiceImpl<ImportTemplateReferenceJPA>
		implements ImportTemplateReferenceService {
	private final ImportTemplateReferenceRepository importTemplateReferenceRepository;

	public DefaultImportTemplateReferenceService(ImportTemplateReferenceRepository importTemplateReferenceRepository) {
		super(importTemplateReferenceRepository);
		this.importTemplateReferenceRepository = importTemplateReferenceRepository;
	}

}
