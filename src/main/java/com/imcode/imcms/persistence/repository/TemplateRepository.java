package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.TemplateJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateJPA, String> {
}
