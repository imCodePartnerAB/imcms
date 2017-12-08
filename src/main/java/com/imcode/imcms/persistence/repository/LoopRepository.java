package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoopRepository extends JpaRepository<LoopJPA, Integer>, VersionedContentRepository<LoopJPA> {

    // TODO: 06.12.17 Investigate why it works with query only
    @Query("SELECT l FROM LoopJPA l WHERE l.version = ?1")
    List<LoopJPA> findByVersion(Version version);

    @Query("SELECT l FROM LoopJPA l WHERE l.version = ?1 AND l.index = ?2")
    LoopJPA findByVersionAndIndex(Version version, int index);

    @Query(value = "SELECT l.* FROM imcms_text_doc_content_loops l WHERE doc_id = ?1", nativeQuery = true)
    List<LoopJPA> findByDocId(Integer docId);
}
