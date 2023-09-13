package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImageHistoryJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageHistoryRepository extends JpaRepository<ImageHistoryJPA, Integer> {

    @Query("select ih from ImageHistoryJPA ih where ih.version.docId = ?1 and ih.loopEntryRef is null and  ih.language = ?2 and ih.index = ?3")
    List<ImageHistoryJPA> findImageHistoryNotInLoop(int docId, LanguageJPA language, int no);

    @Query("select ih from ImageHistoryJPA ih where ih.version.docId = ?1 and ih.language = ?2 and ih.loopEntryRef = ?3 and ih.index = ?4")
    List<ImageHistoryJPA> findImageHistoryInLoop(int docId, LanguageJPA language, LoopEntryRefJPA loopEntryRef, int no);

    /**
     * Clear unnecessary old records if the limit is exceeded
     */
    @Modifying
    @Query(value = "DELETE FROM imcms_text_doc_images_history\n" +
            "WHERE doc_id = ?1\n" +
            "  AND `index` = ?2\n" +
            "  AND language_id = ?3\n" +
            "  AND ((?4 IS NOT NULL AND loop_index = ?4) OR (?4 IS NULL AND loop_index IS NULL))\n" +
            "  AND ((?5 IS NOT NULL AND loop_entry_index = ?5) OR (?5 IS NULL AND loop_entry_index IS NULL))\n" +
            "  AND id NOT IN (SELECT subquery.id FROM (SELECT ih.id\n" +
            "                       FROM imcms_text_doc_images_history ih\n" +
            "                       WHERE ih.doc_id = ?1\n" +
            "                         AND ih.index = ?2\n" +
            "                         AND ih.language_id = ?3\n" +
            "                         AND ((?4 IS NOT NULL AND ih.loop_index = ?4) OR (?4 IS NULL AND ih.loop_index IS NULL))\n" +
            "                         AND ((?5 IS NOT NULL AND ih.loop_index = ?5) OR (?5 IS NULL AND ih.loop_index IS NULL))\n" +
            "                       ORDER BY ih.id DESC\n" +
            "                       LIMIT ?6) AS subquery)",    //use variable(subquery) to be able to limit
            nativeQuery = true)
    void clearHistoryIfLimitExceeded(int docId, int index, int languageId, Integer loopIndex, Integer loopEntryIndex, int limit);
}
