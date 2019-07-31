package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.TemplateGroupJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateGroupRepository extends JpaRepository<TemplateGroupJPA, Integer> {

    TemplateGroupJPA findByName(String name);

    void deleteByName(String name);
}
