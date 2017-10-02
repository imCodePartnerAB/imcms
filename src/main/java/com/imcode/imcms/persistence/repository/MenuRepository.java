package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("com.imcode.imcms.persistence.repository.MenuRepository")
public interface MenuRepository extends JpaRepository<Menu, Integer> {
}
