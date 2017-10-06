package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NULL")
    List<Image> findByVersionAndLanguageWhereLoopEntryRefIsNull(Version version, Language language);

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.loopEntryRef IS NOT NULL")
    List<Image> findByVersionAndLanguageWhereLoopEntryRefIsNotNull(Version version, Language language);


    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.index = ?2 AND i.loopEntryRef IS NULL")
    List<Image> findByVersionAndIndexWhereLoopEntryRefIsNull(Version version, int no);

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.index = ?2 AND i.loopEntryRef = ?3")
    List<Image> findByVersionAndIndexAndLoopEntryRef(Version version, int no, LoopEntryRef loopEntryRef);


    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef IS NULL")
    Image findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, Language language, int no);

    @Query("SELECT i FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef = ?4")
    Image findByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, Language language, int no, LoopEntryRef loopEntryRef);


    @Query("SELECT i.id FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef IS NULL")
    Integer findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(Version version, Language language, int no);

    @Query("SELECT i.id FROM Image i WHERE i.version = ?1 AND i.language = ?2 AND i.index = ?3 AND i.loopEntryRef = ?4")
    Integer findIdByVersionAndLanguageAndIndexAndLoopEntryRef(Version version, Language language, int no, LoopEntryRef loopEntryRef);

    @Query("SELECT i FROM Image i WHERE i.generatedFilename != null AND i.generatedFilename != '' ORDER BY i.id DESC")
    Collection<Image> findAllGeneratedImages();

    @Modifying
    @Query("DELETE FROM Image i WHERE i.version = ?1 AND i.language = ?2")
    int deleteByVersionAndLanguage(Version version, Language language);
}
