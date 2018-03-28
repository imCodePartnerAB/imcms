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
    @Query("select th from TextHistoryJPA th where th.docId=?1 and th.loopEntryRef is null and  th.language=?2 and th.index = ?3")
    List<TextHistoryJPA> findTextHistoryNotInLoop(int docId, LanguageJPA language, int no);

    @Query("select th from TextHistoryJPA th where th.docId=?1 and th.language=?2 and th.loopEntryRef=?3 and th.index = ?4")
    List<TextHistoryJPA> findTextHistoryInLoop(int docId, LanguageJPA language, LoopEntryRefJPA loopEntryRef, int no);
}
