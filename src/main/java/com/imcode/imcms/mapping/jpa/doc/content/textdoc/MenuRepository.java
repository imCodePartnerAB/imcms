package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Modifying
    @Query("DELETE FROM Menu m WHERE m.version = ?1")
    void deleteByVersion(Version version);

    List<Menu> findByVersion(Version version);

    @Query("SELECT m.id FROM Menu m WHERE m.version = ?2 AND m.no = ?2")
    Integer findIdByVersionAndNo(Version version, int no);
}
