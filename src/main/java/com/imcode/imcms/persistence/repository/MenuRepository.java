package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer>, VersionedContentRepository<Menu> {

    @Query("select menu from Menu menu " +
            "left join fetch menu.menuItems " +
            "where menu.no = ?1 and menu.version = ?2")
    Menu findByNoAndVersionAndFetchMenuItemsEagerly(Integer menuNo, Version version);

    @Query("SELECT DISTINCT m FROM Menu m " +
            "left join fetch m.menuItems " +
            "WHERE m.version = ?1 " +
            "GROUP BY m.id")
    List<Menu> findByVersion(Version version);

    @Query(value = "SELECT m.* FROM imcms_menu m WHERE doc_id = ?1", nativeQuery = true)
    List<Menu> findByDocId(Integer docId);

    @SuppressWarnings("SpringDataMethodInconsistencyInspection")
    default void deleteByDocId(Integer docId) {
        final List<Menu> menus = findByDocId(docId);
        deleteInBatch(menus);
    }

    @Modifying
    @Query("DELETE FROM Menu m WHERE m.version = ?1")
    void deleteByVersion(Version version);

    @Query("SELECT menu FROM Menu menu " +
            "INNER JOIN menu.menuItems item " +
            "WHERE item.documentId = ?1 AND menu.version.no = ?2")
    List<Menu> getDocIdsByLinkedDocIdAndVersionNo(Integer linkedDocId, Integer versionNo);
}
