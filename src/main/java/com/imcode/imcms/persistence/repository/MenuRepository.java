package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("com.imcode.imcms.persistence.repository.MenuRepository")
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Query("select menu from com.imcode.imcms.persistence.entity.Menu menu " +
            "left join fetch menu.menuItems " +
            "where menu.no = ?1 and menu.version = ?2")
    Menu findByNoAndVersionAndFetchMenuItemsEagerly(Integer menuNo, Version version);

    @Query("SELECT m FROM com.imcode.imcms.persistence.entity.Menu m " +
            "left join fetch m.menuItems " +
            "WHERE m.version = ?1 " +
            "GROUP BY m.id")
    List<Menu> findByVersion(Version version);

}
