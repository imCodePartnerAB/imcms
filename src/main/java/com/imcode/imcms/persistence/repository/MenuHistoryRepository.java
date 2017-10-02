package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.MenuHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("com.imcode.imcms.persistence.repository.MenuHistoryRepository")
public interface MenuHistoryRepository extends JpaRepository<MenuHistory, Integer> {
}
