package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.TextDocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextDocumentTemplateRepository extends JpaRepository<TextDocumentTemplate, Integer> {
}
