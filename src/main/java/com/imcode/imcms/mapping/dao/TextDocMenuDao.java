package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocVersion;
import com.imcode.imcms.mapping.orm.TextDocMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextDocMenuDao extends JpaRepository<TextDocMenu, Integer> {

    @Modifying
    @Query("DELETE FROM TextDocMenu m WHERE m.docVersion = ?1")
    void deleteByDocVersion(DocVersion docVersion);

    List<TextDocMenu> getByDocVersion(DocVersion docVersion);
}
