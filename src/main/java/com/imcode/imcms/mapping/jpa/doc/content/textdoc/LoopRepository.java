package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoopRepository extends JpaRepository<Loop, Integer> {

    @Query("SELECT l FROM Loop l WHERE l.version = ?1")
    List<Loop> findByVersion(Version version);

    @Query("SELECT l FROM Loop l WHERE l.version = ?1 AND l.no = ?2")
    Loop findByVersionAndNo(Version version, int no);

    @Query("SELECT l.id FROM Loop l WHERE l.version = ?1 AND l.no = ?2")
    Integer findIdByVersionAndNo(Version version, int no);

}
