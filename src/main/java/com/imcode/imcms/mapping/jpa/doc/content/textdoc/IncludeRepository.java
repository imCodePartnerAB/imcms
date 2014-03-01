package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncludeRepository extends JpaRepository<Include, Integer> {

    List<Include> findByDocId(int docId);

    @Modifying
    @Query("DELETE FROM Include i WHERE i.docId = ?1")
    void deleteByDocId(int docId);
}
