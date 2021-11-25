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
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE template SET template_name = ?2 WHERE template_name = ?1", nativeQuery = true)
    void updateTemplateName(String oldTemplateName, String newTemplateName);

    TemplateJPA findByName(String name);

    /**
     * Note: method will delete template-&gt; template group relation but not the template itself
     */
    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "DELETE FROM template_template_group WHERE template_id = ?1", nativeQuery = true)
    void deleteTemplateGroupByTemplateId(int templateId);
}
