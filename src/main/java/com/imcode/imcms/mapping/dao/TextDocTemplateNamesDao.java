package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.TextDocTemplateNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextDocTemplateNamesDao extends JpaRepository<TextDocTemplateNames, Integer> {
}
