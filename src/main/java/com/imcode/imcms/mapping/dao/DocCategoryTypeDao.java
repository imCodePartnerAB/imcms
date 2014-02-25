package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocCategoryType;
import com.imcode.imcms.mapping.orm.DocLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocCategoryTypeDao extends JpaRepository<DocCategoryType, Integer> {

    DocCategoryType findByName(String name);

    DocCategoryType findByNameIgnoreCase(String name);
}
