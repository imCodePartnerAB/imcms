package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer>, VersionedContentRepository<Image> {

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NULL")
    List<Image> findByVersionAndLanguageWhereLoopEntryRefIsNull(Version version, LanguageJPA language);

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NOT NULL")
    List<Image> findByVersionAndLanguageWhereLoopEntryRefIsNotNull(Version version, LanguageJPA language);


    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.index = ?2 AND i.loopEntryRef IS NULL")
    List<Image> findByVersionAndIndexWhereLoopEntryRefIsNull(Version version, int index);

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.index = ?2 AND i.loopEntryRef = ?3")
    List<Image> findByVersionAndIndexAndLoopEntryRef(Version version, int index, LoopEntryRefJPA loopEntryRef);


    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef IS NULL")
    Image findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, LanguageJPA language, int index);

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef = ?4")
    Image findByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, LanguageJPA language, int index, LoopEntryRefJPA loopEntryRef);


    @Query("SELECT i.id FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef IS NULL")
    Integer findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, LanguageJPA language, int index);

    @Query("SELECT i.id FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef = ?4")
    Integer findIdByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, LanguageJPA language, int index, LoopEntryRefJPA loopEntryRef);

    @Query("SELECT i FROM Image i WHERE i.generatedFilename != null AND i.generatedFilename != '' ORDER BY i.id DESC")
    Collection<Image> findAllGeneratedImages();

    @Override
    @Query("SELECT i FROM Image i WHERE i.version = ?1")
    List<Image> findByVersion(Version version);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.version = ?1 AND i.language = ?2")
    void deleteByVersionAndLanguage(Version version, LanguageJPA language);

    @Modifying
    @Query(value = "DELETE FROM imcms_text_doc_images WHERE doc_id = ?1", nativeQuery = true)
    void deleteByDocId(Integer docId);
}
