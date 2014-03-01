package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateNamesRepository extends JpaRepository<TemplateNames, Integer> {
}
