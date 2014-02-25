package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DocPropertyDao extends JpaRepository<DocProperty, Integer> {

    DocProperty findByDocIdAndName(int docId, String name);
}
