package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

    Property findByDocIdAndName(int docId, String name);

    List<Property> findByDocId(int docId);

    @Query("SELECT p.docId FROM Property p WHERE lower(p.value) = lower(?1)")
    Integer findDocIdByAlias(String alias);

    @Query("SELECT lower(p.value) FROM Property p WHERE p.name = 'imcms.document.alias' ORDER BY 1")
    List<String> findAllAliases();
}
