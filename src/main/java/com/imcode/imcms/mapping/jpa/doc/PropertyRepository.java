package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

    Property findByDocIdAndName(int docId, String name);

    @Query("SELECT p.docId FROM Property p WHERE p.name = 'imcms.document.alias' AND p.value = ?1")
    Integer findDocumentIdByAlias(String alias);
}
