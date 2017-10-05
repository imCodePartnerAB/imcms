package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Loop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoopRepository extends JpaRepository<Loop, Integer> {

    @Query("SELECT l FROM Loop l WHERE l.version = ?1")
    List<Loop> findByVersion(Version version);

    @Query("SELECT l FROM Loop l WHERE l.version = ?1 AND l.index = ?2")
    Loop findByVersionAndIndex(Version version, int index);

    @Query("SELECT l.id FROM Loop l WHERE l.version = ?1 AND l.index = ?2")
    Integer findIdByVersionAndIndex(Version version, int index);

}
