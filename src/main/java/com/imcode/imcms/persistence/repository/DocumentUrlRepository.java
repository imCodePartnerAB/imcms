package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentUrlRepository extends JpaRepository<DocumentUrlJPA, Integer> {

    @Modifying
    @Query("DELETE FROM DocumentUrlJPA c WHERE c.version.docId = ?1 AND c.version.no = ?2")
    void deleteByDocIdAndVersionNo(int docId, int versionNo);

    @Query("SELECT c FROM DocumentUrlJPA c WHERE c.version.docId = ?1 AND c.version.no = ?2")
    DocumentUrlJPA findByDocIdAndVersionNo(int docId, int versionNo);
}

