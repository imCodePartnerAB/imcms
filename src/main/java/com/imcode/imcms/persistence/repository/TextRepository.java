package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextRepository extends JpaRepository<TextJPA, Integer> {

    @Query("SELECT t FROM TextJPA t WHERE t.version = ?1 AND t.language = ?2 AND t.loopEntryRef IS NULL")
    List<TextJPA> findByVersionAndLanguageWhereLoopEntryRefIsNull(Version version, LanguageJPA language);

    @Query("SELECT t FROM TextJPA t WHERE t.version = ?1 AND t.language = ?2 AND t.loopEntryRef IS NOT NULL")
    List<TextJPA> findByVersionAndLanguageWhereLoopEntryRefIsNotNull(Version version, LanguageJPA language);

    @Query("SELECT t FROM TextJPA t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef IS NULL")
    TextJPA findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, LanguageJPA language, int no);

    @Query("SELECT t FROM TextJPA t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef = ?4")
    TextJPA findByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, LanguageJPA language, int index, LoopEntryRefJPA loopEntryRef);

    @Query("SELECT t.id FROM TextJPA t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef IS NULL")
    Integer findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, LanguageJPA language, int no);

    @Query("SELECT t.id FROM TextJPA t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef = ?4")
    Integer findIdByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, LanguageJPA language, int no, LoopEntryRefJPA loopEntryRef);


    @Modifying
    @Query("DELETE FROM TextJPA t WHERE t.version = ?1 AND t.language = ?2")
    int deleteByVersionAndLanguage(Version version, LanguageJPA language);
}
