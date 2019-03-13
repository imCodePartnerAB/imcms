package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImageHistoryJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageHistoryRepository extends JpaRepository<ImageHistoryJPA, Integer> {

    @Query("select ih from ImageHistoryJPA ih where ih.version.docId = ?1 and ih.loopEntryRef is null and  ih.language = ?2 and ih.index = ?3")
    List<ImageHistoryJPA> findImageHistoryNotInLoop(int docId, LanguageJPA language, int no);

    @Query("select ih from ImageHistoryJPA ih where ih.version.docId = ?1 and ih.language = ?2 and ih.loopEntryRef = ?3 and ih.index = ?4")
    List<ImageHistoryJPA> findImageHistoryInLoop(int docId, LanguageJPA language, LoopEntryRefJPA loopEntryRef, int no);
}
