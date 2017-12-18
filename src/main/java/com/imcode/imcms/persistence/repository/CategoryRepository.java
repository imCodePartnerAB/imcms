package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryJPA, Integer> {

    List<CategoryJPA> findByType(CategoryTypeJPA type);

    CategoryJPA findByNameAndType(String name, CategoryTypeJPA type);

    @Query(value = "select meta_id from document_categories where category_id = ?", nativeQuery = true)
    String[] findCategoryDocIds(int categoryId);

    @Modifying
    @Query(value = "DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", nativeQuery = true)
    void deleteByDocIdAndCategoryId(int docId, int categoryId);
}
