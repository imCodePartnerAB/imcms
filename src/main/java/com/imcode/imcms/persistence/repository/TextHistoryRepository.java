package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistory;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextHistoryRepository extends JpaRepository<TextHistory, Integer> {
    @Query("SELECT th FROM TextHistory th WHERE th.index = ?3 AND th.version = ?1 AND th.language=?2 AND th.loopEntryRef=null")
    List<TextHistory> findAllByVersionAndLanguageAndNo(Version version, LanguageJPA language, int no);

    @Query("SELECT th FROM TextHistory th WHERE th.index = ?4 AND th.version = ?1 AND th.language=?2 AND th.loopEntryRef=?3")
    List<TextHistory> findAllByVersionAndLanguageAndLoopEntryRefAndNo(Version version, LanguageJPA language, LoopEntryRefJPA loopEntryRef, int no);
}
