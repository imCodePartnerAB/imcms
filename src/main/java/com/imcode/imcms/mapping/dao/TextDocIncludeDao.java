package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.TextDocInclude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextDocIncludeDao extends JpaRepository<TextDocInclude, Integer> {

    List<TextDocInclude> findByDocId(int docId);

    @Modifying
    @Query("DELETE FROM TextDocInclude i WHERE i.docId = ?1")
    void deleteByDocId(int docId);
}
