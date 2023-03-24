package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImportRoleReferenceJPA;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportRoleReferenceRepository extends ImportEntityReferenceRepository<ImportRoleReferenceJPA> {

	@Override
	ImportRoleReferenceJPA findByName(String name);

	@Override
	boolean existsByName(String name);
}
