package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocLanguage;
import com.imcode.imcms.mapping.orm.DocVersion;
import com.imcode.imcms.mapping.orm.TextDocLoopEntryRef;
import com.imcode.imcms.mapping.orm.TextDocLoopEntryRef;
import com.imcode.imcms.mapping.orm.TextDocText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextDocTextDao extends JpaRepository<TextDocText, Integer> {

    @Query("SELECT l FROM TextDocText l WHERE l.docVersion = ?1")
    List<TextDocText> findByDocVersion(DocVersion version);

    @Query("SELECT l FROM TextDocText l WHERE l.docVersion = ?1 AND l.docLanguage = ?2")
    List<TextDocText> findByDocVersionAndDocLanguage(DocVersion version, DocLanguage docLanguage);


    @Query("SELECT l FROM TextDocText l WHERE l.docVersion = ?1 AND l.no = ?2 AND l.loopEntryRef IS NULL")
    List<TextDocText> findByDocVersionAndNoAndLoopEntryRefIsNull(DocVersion version, int no);

    @Query("SELECT l FROM TextDocText l WHERE l.docVersion = ?1 AND l.no = ?2 AND l.loopEntryRef = ?3")
    List<TextDocText> findByDocVersionAndNoAndLoopEntryRef(DocVersion version, int no, TextDocLoopEntryRef loopEntryRef);


    @Query("SELECT l FROM TextDocText l WHERE l.docVersion = ?1 AND l.docLanguage = ?2 AND l.no = ?3 AND l.loopEntryRef IS NULL")
    TextDocText findByDocVersionAndDocLanguageAndNoAndLoopEntryIsNull(DocVersion version, DocLanguage docLanguage, int no);

    @Query("SELECT l FROM TextDocText l WHERE l.docVersion = ?1 AND l.docLanguage = ?2 AND l.no = ?3 AND l.loopEntryRef = ?4")
    TextDocText findByDocVersionAndDocLanguageAndNoAndLoopEntryRef(DocVersion version, DocLanguage docLanguage, int no, TextDocLoopEntryRef loopEntryRef);


    @Modifying
    @Query("DELETE FROM TextDocText l WHERE l.docVersion = ?1 AND l.docLanguage = ?2")
    int deleteByDocVersionAndDocLanguage(DocVersion version, DocLanguage docLanguage);
}
