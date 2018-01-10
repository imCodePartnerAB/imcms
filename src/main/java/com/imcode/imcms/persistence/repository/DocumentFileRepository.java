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

    //    @Query("SELECT f FROM DocumentFileJPA f WHERE f.docId = ?1 ORDER BY f.defaultFileId DESC, f.fileId")
    List<DocumentFileJPA> findByDocId(int docId);

    List<DocumentFileJPA> findByDocIdAndVersionIndex(int docId, int versionIndex);

    @Query("select f from DocumentFileJPA f" +
            " where f.docId = ?1 and f.versionIndex = ?2 and f.defaultFile = true" +
            " order by f.defaultFile desc, f.fileId")
    DocumentFileJPA findDefaultByDocIdAndVersionIndex(int docId, int versionIndex);

}
