package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocLanguage;
import com.imcode.imcms.mapping.orm.DocVersion;
import com.imcode.imcms.mapping.orm.TextDocImage;
import com.imcode.imcms.mapping.orm.TextDocLoopEntryRef;
import com.imcode.imcms.mapping.orm.TextDocLoopEntryRef;
import imcode.server.document.textdocument.ImageDomainObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextDocImageDao extends JpaRepository<TextDocImage, Integer> {

    //@Query("SELECT l FROM TextDocImage l WHERE l.docVersion = ?1")
    List<TextDocImage> findByDocVersion(DocVersion version);

    //@Query("SELECT l FROM TextDocImage l WHERE l.docVersion = ?1 AND l.docLanguage = ?2")
    List<TextDocImage> findByDocVersionAndDocLanguage(DocVersion version, DocLanguage docLanguage);


    @Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.no = ?2 AND l.loopEntry IS NULL")
    List<TextDocImage> findByDocVersionAndNoAndLoopEntryIsNull(DocVersion version, int no);

    //@Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.no = ?2 AND l.loopEntry = ?3")
    List<TextDocImage> findByDocVersionAndNoAndLoopEntry(DocVersion version, int no, TextDocLoopEntryRef loopEntry);


    @Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.docLanguage = ?2 AND l.no = ?3 AND l.loopEntry IS NULL")
    TextDocImage findByDocVersionAndDocLanguageAndNoAndLoopEntryIsNull(DocVersion version, DocLanguage docLanguage, int no);

    //@Query("SELECT l FROM TextDocImage l WHERE AND l.docVersion = ?1 AND l.docLanguage = ?2 AND l.no = ?3 AND l.loopEntry = ?4")
    TextDocImage findByDocVersionAndDocLanguageAndNoAndLoopEntry(DocVersion version, DocLanguage docLanguage, int no, TextDocLoopEntryRef loopEntry);
}
