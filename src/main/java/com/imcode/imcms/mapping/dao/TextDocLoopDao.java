package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocVersion;
import com.imcode.imcms.mapping.orm.TextDocLoop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextDocLoopDao extends JpaRepository<TextDocLoop, Integer> {
//public interface TextDocLoopDao extends DocVersionedMultiContentRepository<TextDocLoop, Integer> {

    @Query("SELECT l FROM TextDocLoop l WHERE l.docVersion = ?1")
    List<TextDocLoop> findByDocVersion(DocVersion version);

    @Query("SELECT l FROM TextDocLoop l WHERE l.docVersion = ?1 AND l.no = ?2")
    TextDocLoop findByDocVersionAndNo(DocVersion version, int no);
}
