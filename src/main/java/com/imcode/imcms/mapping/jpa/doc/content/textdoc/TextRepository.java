package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TextRepository extends JpaRepository<Text, Integer>, TextRepositoryCustom {

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.loopEntryRef IS NULL")
    List<Text> findByVersionAndLanguageWhereLoopEntryRefIsNull(Version version, Language language);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.loopEntryRef IS NOT NULL")
    List<Text> findByVersionAndLanguageWhereLoopEntryRefIsNotNull(Version version, Language language);


    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.no = ?2 AND t.loopEntryRef IS NULL")
    List<Text> findByVersionAndNoWhereLoopEntryRefIsNull(Version version, int no);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.no = ?2 AND t.loopEntryRef = ?3")
    List<Text> findByVersionAndNoAndLoopEntryRef(Version version, int no, LoopEntryRef loopEntryRef);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.no = ?3 AND t.loopEntryRef IS NULL")
    Text findByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(Version version, Language language, int no);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.no = ?3 AND t.loopEntryRef = ?4")
    Text findByVersionAndLanguageAndNoAndLoopEntryRef(Version version, Language language, int no, LoopEntryRef loopEntryRef);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.no = ?3 AND t.documentId = ?4 AND t.loopEntryRef = ?5")
    Text findByVersionAndLanguageAndNoAndDocumentIdAndLoopEntryRef(Version version, Language language, int no, int docId, LoopEntryRef loopEntryRef);

    @Query("SELECT t FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.no = ?3 AND t.documentId = ?4 AND t.loopEntryRef IS NULL")
    Text findByVersionAndLanguageAndNoAndDocumentIdWhereLoopEntryRefIsNull(Version version, Language language, int no, int docId);

    @Query("SELECT t.id FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.no = ?3 AND t.loopEntryRef IS NULL")
    Integer findIdByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(Version version, Language language, int no);

    @Query("SELECT t.id FROM Text t WHERE t.version = ?1 AND t.language = ?2 AND t.no = ?3 AND t.loopEntryRef = ?4")
    Integer findIdByVersionAndLanguageAndNoAndLoopEntryRef(Version version, Language language, int no, LoopEntryRef loopEntryRef);


    @Modifying
    @Query("DELETE FROM Text t WHERE t.version = ?1 AND t.language = ?2")
    int deleteByVersionAndLanguage(Version version, Language language);
}
