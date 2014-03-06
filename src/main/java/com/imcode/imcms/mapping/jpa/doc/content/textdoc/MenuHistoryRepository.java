package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuHistoryRepository extends JpaRepository<MenuHistory, Integer> {
}
