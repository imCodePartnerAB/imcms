package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryTypeJPA, Integer> {

    CategoryTypeJPA findByNameIgnoreCase(String name);

	Optional<CategoryType> findByName(String name);

	boolean existsByName(String name);
}
