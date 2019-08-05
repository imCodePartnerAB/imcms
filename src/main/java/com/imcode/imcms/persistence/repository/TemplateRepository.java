package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.TemplateJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateJPA, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE template SET template_name = ?1 WHERE template_name = ?2", nativeQuery = true)
    void updateTemplateName(String newTemplateName, String oldTemplateName);

    TemplateJPA findByName(String name);

}
