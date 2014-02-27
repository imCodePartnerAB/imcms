package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    Language findByCode(String code);

    @Modifying
    @Query("DELETE FROM DocLanguage l WHERE l.code = ?1")
    int deleteByCode(String code);
}
