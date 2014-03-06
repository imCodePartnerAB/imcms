package com.imcode.imcms.mapping.jpa.doc.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlDocContentRepository extends JpaRepository<UrlDocContent, Integer> {
}

