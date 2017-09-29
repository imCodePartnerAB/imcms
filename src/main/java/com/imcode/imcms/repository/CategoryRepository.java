package com.imcode.imcms.repository;

import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByType(CategoryType type);

    Category findByNameAndType(String name, CategoryType type);

    @Query(value = "select meta_id from document_categories where category_id = ?", nativeQuery = true)
    String[] findCategoryDocIds(int categoryId);

    @Modifying
    @Query(value = "DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", nativeQuery = true)
    String[] deleteByDocIdAndCategoryId(int docId, int categoryId);
}
