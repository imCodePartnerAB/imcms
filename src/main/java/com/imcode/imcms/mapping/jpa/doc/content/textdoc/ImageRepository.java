package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    //@Query("SELECT l FROM TextDocImage l WHERE l.docVersion = ?1")
    List<Image> findByDocVersion(DocVersion docVersion);

    //@Query("SELECT l FROM TextDocImage l WHERE l.docVersion = ?1 AND l.docLanguage = ?2")
    List<Image> findByDocVersionAndDocLanguage(DocVersion docVersion, Language language);


    @Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.no = ?2 AND l.loopEntry IS NULL")
    List<Image> findByDocVersionAndNoAndLoopEntryIsNull(DocVersion docVersion, int no);

    //@Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.no = ?2 AND l.loopEntry = ?3")
    List<Image> findByDocVersionAndNoAndLoopEntry(DocVersion docVersion, int no, LoopEntryRef loopEntry);


    @Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.docLanguage = ?2 AND l.no = ?3 AND l.loopEntry IS NULL")
    Image findByDocVersionAndDocLanguageAndNoAndLoopEntryIsNull(DocVersion docVersion, Language language, int no);

    //@Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.docLanguage = ?2 AND l.no = ?3 AND l.loopEntry = ?4")
    Image findByDocVersionAndDocLanguageAndNoAndLoopEntry(DocVersion docVersion, Language language, int no, LoopEntryRef loopEntry);

    int deleteByDocVersionAndDocLanguage(DocVersion docVersion, Language language);
}
