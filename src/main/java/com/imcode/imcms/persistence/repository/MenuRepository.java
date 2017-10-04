package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("com.imcode.imcms.persistence.repository.MenuRepository")
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Query("select menu from com.imcode.imcms.persistence.entity.Menu menu " +
            "join fetch menu.menuItems " +
            "where menu.no = ?1 and menu.version = ?2")
    public Menu findByNoAndVersionAndFetchMenuItemsEagerly(Integer menuNo, Version version);

}
