package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextHistoryRepository extends JpaRepository<TextHistoryJPA, Integer> {
    @Query("select th from TextHistoryJPA th where th.docId=?1 and th.loopEntryRef is null and  th.language=?2 and th.index = ?3")
    List<TextHistoryJPA> findTextHistoryNotInLoop(int docId, LanguageJPA language, int no);

    @Query("select th from TextHistoryJPA th where th.docId=?1 and th.language=?2 and th.loopEntryRef=?3 and th.index = ?4")
    List<TextHistoryJPA> findTextHistoryInLoop(int docId, LanguageJPA language, LoopEntryRefJPA loopEntryRef, int no);

    /**
     * Clear unnecessary old records if the limit is exceeded
     */
    @Modifying
    @Query(value = "DELETE FROM imcms_text_doc_texts_history\n" +
            "WHERE doc_id = ?1\n" +
            "  AND `index` = ?2\n" +
            "  AND language_id = ?3\n" +
            "  AND ((?4 IS NOT NULL AND loop_index = ?4) OR (?4 IS NULL AND loop_index IS NULL))\n" +
            "  AND ((?5 IS NOT NULL AND loop_entry_index = ?5) OR (?5 IS NULL AND loop_entry_index IS NULL))\n" +
            "  AND id NOT IN (SELECT subquery.id FROM (SELECT th.id\n" +
            "                       FROM imcms_text_doc_texts_history th\n" +
            "                       WHERE th.doc_id = ?1\n" +
            "                         AND th.index = ?2\n" +
            "                         AND th.language_id = ?3\n" +
            "                         AND ((?4 IS NOT NULL AND th.loop_index = ?4) OR (?4 IS NULL AND th.loop_index IS NULL))\n" +
            "                         AND ((?5 IS NOT NULL AND th.loop_index = ?5) OR (?5 IS NULL AND th.loop_index IS NULL))\n" +
            "                       ORDER BY th.id DESC\n" +
            "                       LIMIT ?6) AS subquery)",    //use variable(subquery) to be able to limit
            nativeQuery = true)
    void clearHistoryIfLimitExceeded(int docId, int index, int languageId, Integer loopIndex, Integer loopEntryIndex, int count);
}
