package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Text;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextRepository extends JpaRepository<Text, Integer> {

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.loopEntryRef IS NULL")
    List<Text> findByVersionAndLanguageWhereLoopEntryRefIsNull(Version version, Language language);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.loopEntryRef IS NOT NULL")
    List<Text> findByVersionAndLanguageWhereLoopEntryRefIsNotNull(Version version, Language language);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef IS NULL")
    Text findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, Language language, int no);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef = ?4")
    Text findByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, Language language, int index, LoopEntryRef loopEntryRef);

    @Query("SELECT t.id FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef IS NULL")
    Integer findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, Language language, int no);

    @Query("SELECT t.id FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.index = ?3 AND t.loopEntryRef = ?4")
    Integer findIdByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, Language language, int no, LoopEntryRef loopEntryRef);


    @Modifying
    @Query("DELETE FROM Text t WHERE t.version = ?1 AND t.language = ?2")
    int deleteByVersionAndLanguage(Version version, Language language);
}
