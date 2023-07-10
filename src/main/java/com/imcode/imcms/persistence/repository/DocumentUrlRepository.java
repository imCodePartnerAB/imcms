package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentUrlRepository extends JpaRepository<DocumentUrlJPA, Integer>, VersionedContentRepository<DocumentUrlJPA> {

    @Query("SELECT c FROM DocumentUrlJPA c WHERE c.version.docId = ?1 AND c.version.no = ?2")
    DocumentUrlJPA findByDocIdAndVersionNo(int docId, int versionNo);

    @Query("SELECT c FROM DocumentUrlJPA c WHERE c.version.docId = ?1")
    List<DocumentUrlJPA> findByDocId(int docId);

    @Override
    @Query("SELECT d FROM DocumentUrlJPA d WHERE d.version = ?1")
    List<DocumentUrlJPA> findByVersion(Version version);

    List<DocumentUrlJPA> findAllByUrlContains(String content);
}
