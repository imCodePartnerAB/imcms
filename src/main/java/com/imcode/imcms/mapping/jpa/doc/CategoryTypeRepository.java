package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, Integer> {

    CategoryType findByName(String name);

    CategoryType findByNameIgnoreCase(String name);
}
