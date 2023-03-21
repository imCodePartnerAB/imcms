package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImportCategoryTypeReferenceJPA;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportCategoryTypeReferenceRepository extends ImportEntityReferenceRepository<ImportCategoryTypeReferenceJPA> {
	@Override
	ImportCategoryTypeReferenceJPA findByName(String name);

	@Override
	boolean existsByName(String name);
}
