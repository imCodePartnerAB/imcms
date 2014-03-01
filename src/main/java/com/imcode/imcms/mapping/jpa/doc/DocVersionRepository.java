package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocVersionRepository extends JpaRepository<DocVersion, Integer>, DocVersionRepositoryCustom {

    List<DocVersion> findByDocId(int docId);

    DocVersion findByDocIdAndNo(int docId, int no);

    @Query(name = "DocVersion.findDefault")
    DocVersion findDefault(int docId);

    @Query(name = "DocVersion.findLatest")
    DocVersion findLatest(int docId);
}

