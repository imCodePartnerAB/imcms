package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocVersionRepository extends JpaRepository<Version, Integer>, DocVersionRepositoryCustom {

    @Query(name = "DocVersion.getByDocIdOrderByNo")
    List<Version> findByDocId(int docId);

    Version findByDocIdAndNo(int docId, int no);

    @Query(name = "DocVersion.findWorking")
    Version findWorking(int docId);

    @Query(name = "DocVersion.findDefault")
    Version findDefault(int docId);

    @Query(name = "DocVersion.findLatest")
    Version findLatest(int docId);



}

