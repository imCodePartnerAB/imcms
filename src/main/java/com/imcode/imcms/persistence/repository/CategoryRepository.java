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

    @Query(value = "select c.* from categories c where category_type_id = ?1", nativeQuery = true)
    List<CategoryJPA> findById(int id);

    CategoryJPA findByNameAndType(String name, CategoryTypeJPA type);

    @Query(value = "select meta_id from document_categories where category_id = ?1", nativeQuery = true)
    List<Integer> findCategoryDocIds(int categoryId);

    /**
     * Note: method will delete document-&gt; category relation but not the category itself
     */
    @Modifying
    @Query(value = "DELETE FROM document_categories WHERE meta_id = ?1 and category_id = ?2", nativeQuery = true)
    void deleteByDocIdAndCategoryId(int docId, int categoryId);

    /**
     * Note: method will delete document-&gt; category relation but not the category itself
     */
    @Modifying
    @Query(value = "DELETE FROM document_categories WHERE category_id = ?1", nativeQuery = true)
    void deleteDocumentCategory(int categoryId);
}
