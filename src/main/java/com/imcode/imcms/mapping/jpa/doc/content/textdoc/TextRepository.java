package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextRepository extends JpaRepository<Text, Integer> {

    @Query("SELECT l FROM Text l WHERE l.docVersion = ?1 AND l.language = ?2 AND l.loopEntryRef IS NULL")
    List<Text> findByDocVersionAndLanguageAndLoopEntryRefIsNull(DocVersion docVersion, Language language);

    @Query("SELECT l FROM Text l WHERE l.docVersion = ?1 AND l.language = ?2 AND l.loopEntryRef IS NOT NULL")
    List<Text> findByDocVersionAndLanguageAndLoopEntryRefIsNotNull(DocVersion docVersion, Language language);

    @Query("SELECT l FROM Text l WHERE l.docVersion = ?1 AND l.language = ?2 AND l.loopEntryRef = ?3")
    List<Text> findByDocVersionAndLanguageAndLoopEntryRef(DocVersion docVersion, Language language, LoopEntryRef loopEntryRef);


    @Query("SELECT l FROM Text l WHERE l.docVersion = ?1 AND l.no = ?2 AND l.loopEntryRef IS NULL")
    List<Text> findByDocVersionAndNoAndLoopEntryRefIsNull(DocVersion docVersion, int no);

    @Query("SELECT l FROM Text l WHERE l.docVersion = ?1 AND l.no = ?2 AND l.loopEntryRef = ?3")
    List<Text> findByDocVersionAndNoAndLoopEntryRef(DocVersion docVersion, int no, LoopEntryRef loopEntryRef);


    @Query("SELECT l FROM Text l WHERE l.docVersion = ?1 AND l.language = ?2 AND l.no = ?3 AND l.loopEntryRef IS NULL")
    Text findByDocVersionAndLanguageAndNoAndLoopEntryIsNull(DocVersion docVersion, Language language, int no);

    @Query("SELECT l FROM Text l WHERE l.docVersion = ?1 AND l.language = ?2 AND l.no = ?3 AND l.loopEntryRef = ?4")
    Text findByDocVersionAndLanguageAndNoAndLoopEntryRef(DocVersion docVersion, Language language, int no, LoopEntryRef loopEntryRef);


    @Modifying
    @Query("DELETE FROM Text l WHERE l.docVersion = ?1 AND l.language = ?2")
    int deleteByDocVersionAndLanguage(DocVersion docVersion, Language language);
}
