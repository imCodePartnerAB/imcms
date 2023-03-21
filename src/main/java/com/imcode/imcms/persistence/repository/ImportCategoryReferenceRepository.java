package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImportCategoryReferenceJPA;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportCategoryReferenceRepository extends ImportEntityReferenceRepository<ImportCategoryReferenceJPA> {

	@Override
	ImportCategoryReferenceJPA findByName(String name);

	@Override
	boolean existsByName(String name);
}
