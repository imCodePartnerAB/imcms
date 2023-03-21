package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.AbstractImportEntityReferenceServiceImpl;
import com.imcode.imcms.domain.service.ImportRoleReferenceService;
import com.imcode.imcms.persistence.entity.ImportRoleReferenceJPA;
import com.imcode.imcms.persistence.repository.ImportRoleReferenceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class DefaultImportRoleReferenceService extends AbstractImportEntityReferenceServiceImpl<ImportRoleReferenceJPA>
		implements ImportRoleReferenceService {
	private ImportRoleReferenceRepository importRoleReferenceRepository;

	public DefaultImportRoleReferenceService(ImportRoleReferenceRepository importRoleReferenceRepository) {
		super(importRoleReferenceRepository);
		this.importRoleReferenceRepository = importRoleReferenceRepository;
	}

}
