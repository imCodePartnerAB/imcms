package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryTypeJPA, Integer> {

    CategoryTypeJPA findByNameIgnoreCase(String name);

}
