package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextHistoryRepository extends JpaRepository<TextHistoryJPA, Integer> {
    @Query("SELECT th FROM TextHistoryJPA th WHERE th.loopEntryRef=null AND  th.language=?1 AND th.index = ?2")
    List<TextHistoryJPA> findAllByLanguageAndNo(LanguageJPA language, int no);

    @Query("SELECT th FROM TextHistoryJPA th WHERE th.language=?1 AND th.loopEntryRef=?2 AND th.index = ?3")
    List<TextHistoryJPA> findAllByLanguageAndLoopEntryRefAndNo(LanguageJPA language, LoopEntryRefJPA loopEntryRef, int no);
}
