package com.imcode.imcms.repository;

import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, Integer> {

    CategoryType findByNameIgnoreCase(String name);

}
