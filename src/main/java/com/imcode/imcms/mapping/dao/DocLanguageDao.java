package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocLanguage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DocLanguageDao extends CrudRepository<DocLanguage, Integer> {

    DocLanguage getByCode(String code);

    @Modifying
    @Query("DELETE FROM DocLanguage l WHERE l.code = ?1")
    int deleteByCode(String code);

    List<DocLanguage> findAll();
}
