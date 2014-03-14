package com.imcode.imcms.mapping.jpa.doc.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlDocContentRepository extends JpaRepository<UrlDocContent, Integer> {

    @Modifying
    @Query("DELETE FROM UrlDocContent c WHERE c.version.docId = ?1 AND c.version.no = ?2")
    void deleteByDocIdAndVersionNo(int docId, int versionNo);

    @Query("SELECT c FROM UrlDocContent c WHERE c.version.docId = ?1 AND c.version.no = ?2")
    UrlDocContent findByDocIdAndVersionNo(int docId, int versionNo);
}

