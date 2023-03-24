package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.model.AbstractImportEntityReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ImportEntityReferenceRepository<T extends AbstractImportEntityReference> extends JpaRepository<T, Integer> {
	T findByName(String name);

	boolean existsByName(String name);
}
