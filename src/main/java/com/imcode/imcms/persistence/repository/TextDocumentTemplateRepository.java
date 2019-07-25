package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextDocumentTemplateRepository extends JpaRepository<TextDocumentTemplateJPA, Integer> {

    void deleteByDocId(Integer docId);

    @Query(value = "SELECT meta_id FROM text_docs WHERE template_name = ?1", nativeQuery = true)
    List<Integer> findDocIdByTemplateName(String templateName);

    @Query(value = "SELECT * FROM text_docs WHERE template_name = ?1", nativeQuery = true)
    List<TextDocumentTemplateJPA> findTextDocumentTemplateByTemplateName(String templateName);

}
