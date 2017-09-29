package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, Integer> {

    CategoryType findByNameIgnoreCase(String name);

}
