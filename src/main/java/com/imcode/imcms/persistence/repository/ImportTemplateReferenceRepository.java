package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImportTemplateReferenceJPA;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportTemplateReferenceRepository extends ImportEntityReferenceRepository<ImportTemplateReferenceJPA> {

	@Override
	ImportTemplateReferenceJPA findByName(String name);

	@Override
	boolean existsByName(String name);
}
