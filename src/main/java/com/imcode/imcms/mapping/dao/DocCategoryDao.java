package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocCategory;
import com.imcode.imcms.mapping.orm.DocCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocCategoryDao extends JpaRepository<DocCategory, Integer> {

    List<DocCategory> findByType(DocCategoryType type);

    DocCategory findByNameAndType(String name, DocCategoryType type);

    @Query(value = "select meta_id from document_categories where category_id = ?", nativeQuery = true)
    String[] findCategoryDocIds(int categoryId);

    @Modifying
    @Query(value = "DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", nativeQuery = true)
    String[] deleteByDocIdAndCategoryId(int docId, int categoryId);
}
