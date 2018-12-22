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
import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer>, VersionedContentRepository<Image> {

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NULL")
    List<Image> findByVersionAndLanguageWhereLoopEntryRefIsNull(Version version, LanguageJPA language);

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NOT NULL")
    List<Image> findByVersionAndLanguageWhereLoopEntryRefIsNotNull(Version version, LanguageJPA language);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query("select i.linkUrl from Image i where i.version = ?1 and i.language = ?2 and i.linkUrl > ''")
    Set<String> findNonEmptyImageLinkUrlByVersionAndLanguage(Version version, LanguageJPA language);

    Set<Image> findByVersionAndLanguage(Version version, LanguageJPA language);

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

    @Query("SELECT i " +
            "FROM Image i " +
            "WHERE i.generatedFilename IS NOT NULL AND i.generatedFilename <> '' " +
            "   OR i.url IS NOT NULL AND i.url <> '' " +
            "ORDER BY i.id DESC")
    Collection<Image> findAllRegenerationCandidates();

    @Override
    @Query("SELECT i FROM Image i WHERE i.version = ?1")
    List<Image> findByVersion(Version version);

    List<Image> findByUrl(String url);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.version = ?1 AND i.language = ?2")
    void deleteByVersionAndLanguage(Version version, LanguageJPA language);

    @Modifying
    @Query(value = "DELETE FROM imcms_text_doc_images WHERE doc_id = ?1", nativeQuery = true)
    void deleteByDocId(Integer docId);

    @Query(value = "SELECT MIN(img.index) FROM imcms_text_doc_images img WHERE img.doc_id=?1", nativeQuery = true)
    Integer findMinIndexByVersion(int docId);
}
