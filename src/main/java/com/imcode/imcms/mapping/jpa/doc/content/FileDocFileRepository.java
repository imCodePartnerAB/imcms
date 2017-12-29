package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.persistence.entity.FileDocFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileDocFileRepository extends JpaRepository<FileDocFile, Integer> {

//    @Modifying
//    @Query("DELETE FROM FileDocFile f WHERE f.id IN (SELECT asd FROM (SELECT fdf.id FROM FileDocFile fdf WHERE fdf.version.docId = ?1 AND fdf.version.no = ?2) asd)")
//    void deleteByDocIdAndVersionNo(int docId, int versionNo);

    @Query("SELECT f FROM FileDocFile f WHERE f.version.docId = ?1 AND f.version.no = ?2 ORDER BY f.defaultFileId DESC, f.fileId")
    List<FileDocFile> findByDocIdAndVersionNo(int docId, int versionNo);
}

