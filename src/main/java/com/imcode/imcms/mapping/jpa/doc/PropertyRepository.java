package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

    Property findByDocIdAndName(int docId, String name);

    List<Property> findByDocId(int docId);
}
