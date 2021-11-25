package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.TemplateGroupJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TemplateGroupRepository extends JpaRepository<TemplateGroupJPA, Integer> {

    TemplateGroupJPA findByName(String name);

    void deleteByName(String name);

    /**
     * Note: method will delete template-&gt; template group relation but not the template group itself
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "DELETE FROM template_template_group WHERE group_id = ?1", nativeQuery = true)
    void deleteTemplateGroupByGroupId(int groupId);
}
