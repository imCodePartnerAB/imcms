package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextHistoryRepository extends JpaRepository<TextHistory, Integer> {
    @Query("SELECT th FROM TextHistory th WHERE th.index = ?3 AND th.version = ?1 AND th.language=?2 AND th.loopEntryRef=null")
    List<TextHistory> findAllByVersionAndLanguageAndNo(Version version, Language language, int no);

    @Query("SELECT th FROM TextHistory th WHERE th.index = ?4 AND th.version = ?1 AND th.language=?2 AND th.loopEntryRef=?3")
    List<TextHistory> findAllByVersionAndLanguageAndLoopEntryRefAndNo(Version version, Language language, LoopEntryRef loopEntryRef, int no);
}
