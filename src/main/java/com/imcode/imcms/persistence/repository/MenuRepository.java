package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("com.imcode.imcms.persistence.repository.MenuRepository")
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Override
    @Query("select menu from com.imcode.imcms.persistence.entity.Menu menu " +
            "join fetch menu.menuItems WHERE menu.id = (:id)")
    public Menu getOne(@Param("id") Integer id);

}
