package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, Integer> {

    CategoryType findByNameIgnoreCase(String name);

}
