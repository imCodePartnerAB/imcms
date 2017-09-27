package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, Integer> {

    @Query("select cat_type from CategoryType cat_type join fetch cat_type.categories")
    List<CategoryType> findAllFetchCategoriesEagerly();

    CategoryType findByNameIgnoreCase(String name);

}
