package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.TemplateGroupJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateGroupRepository extends JpaRepository<TemplateGroupJpa, Integer> {

    public TemplateGroupJpa findByName(String name);

}
