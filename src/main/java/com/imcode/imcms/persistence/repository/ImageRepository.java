package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImageJPA;
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
public interface ImageRepository extends JpaRepository<ImageJPA, Integer>, VersionedContentRepository<ImageJPA> {

    @Query("SELECT i FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NULL")
    List<ImageJPA> findByVersionAndLanguageWhereLoopEntryRefIsNull(Version version, LanguageJPA language);

    @Query("SELECT i FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NOT NULL")
    List<ImageJPA> findByVersionAndLanguageWhereLoopEntryRefIsNotNull(Version version, LanguageJPA language);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query("select i.linkUrl from ImageJPA i where i.version = ?1 and i.language = ?2 and i.linkUrl > ''")
    Set<String> findNonEmptyImageLinkUrlByVersionAndLanguage(Version version, LanguageJPA language);

    Set<ImageJPA> findByVersionAndLanguage(Version version, LanguageJPA language);

    @Query("SELECT i FROM ImageJPA i WHERE i.version = ?1 AND i.index = ?2 AND i.loopEntryRef IS NULL")
    List<ImageJPA> findByVersionAndIndexWhereLoopEntryRefIsNull(Version version, int index);

    @Query("SELECT i FROM ImageJPA i WHERE i.version = ?1 AND i.index = ?2 AND i.loopEntryRef = ?3")
    List<ImageJPA> findByVersionAndIndexAndLoopEntryRef(Version version, int index, LoopEntryRefJPA loopEntryRef);

    @Query("SELECT i FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef IS NULL")
    ImageJPA findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, LanguageJPA language, int index);

    @Query("SELECT i FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef = ?4")
    ImageJPA findByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, LanguageJPA language, int index, LoopEntryRefJPA loopEntryRef);

    @Query("SELECT i.id FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef IS NULL")
    Integer findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, LanguageJPA language, int index);

    @Query("SELECT i.id FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef = ?4")
    Integer findIdByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, LanguageJPA language, int index, LoopEntryRefJPA loopEntryRef);

    @Query("SELECT i FROM ImageJPA i " +
            "WHERE (i.generatedFilename IS NOT NULL AND i.generatedFilename <> '' OR i.url IS NOT NULL AND i.url <> '') " +
            "AND (i.version.no = 0 OR i.version.no = (SELECT MAX(i2.version.no) FROM ImageJPA i2 WHERE i.index = i2.index)) " +
            "ORDER BY i.id DESC")
    Collection<ImageJPA> findAllRegenerationCandidates();

    @Override
    @Query("SELECT i FROM ImageJPA i WHERE i.version = ?1")
    List<ImageJPA> findByVersion(Version version);

    List<ImageJPA> findByUrl(String url);

	@Query(value = "SELECT i FROM ImageJPA i WHERE i.url LIKE ?1%")
	List<ImageJPA> findByFolderInUrl(String folder);

    @Modifying
    @Query("DELETE FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2")
    void deleteByVersionAndLanguage(Version version, LanguageJPA language);

    @Modifying
    @Query(value = "DELETE FROM imcms_text_doc_images WHERE doc_id = ?1", nativeQuery = true)
    void deleteByDocId(Integer docId);

    @Query(value = "SELECT MIN(img.index) FROM imcms_text_doc_images img WHERE img.doc_id=?1", nativeQuery = true)
    Integer findMinIndexByVersion(int docId);

	@Query("SELECT i FROM ImageJPA i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef.loopIndex = ?3 ")
	List<ImageJPA> findByVersionAndLanguageAndLoopIndex(Version version, LanguageJPA language, int loopIndex);
}
