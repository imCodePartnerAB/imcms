package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoopRepository extends JpaRepository<Loop, Integer> {
//public interface TextDocLoopRepository extends DocVersionedMultiContentRepository<TextDocLoop, Integer> {

    @Query("SELECT l FROM Loop l WHERE l.docVersion = ?1")
    List<Loop> findByDocVersion(DocVersion docVersion);

    @Query("SELECT l FROM Loop l WHERE l.docVersion = ?1 AND l.no = ?2")
    Loop findByDocVersionAndNo(DocVersion docVersion, int no);
}
