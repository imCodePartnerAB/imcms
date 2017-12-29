package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFileJPA, Integer> {

//    @Modifying
//    @Query("DELETE FROM DocumentFileJPA f WHERE f.id IN (SELECT asd FROM (SELECT fdf.id FROM DocumentFileJPA fdf WHERE fdf.version.docId = ?1 AND fdf.version.no = ?2) asd)")
//    void deleteByDocIdAndVersionNo(int docId, int versionNo);

    @Query("SELECT f FROM DocumentFileJPA f WHERE f.version.docId = ?1 AND f.version.no = ?2 ORDER BY f.defaultFileId DESC, f.fileId")
    List<DocumentFileJPA> findByDocIdAndVersionNo(int docId, int versionNo);
}

