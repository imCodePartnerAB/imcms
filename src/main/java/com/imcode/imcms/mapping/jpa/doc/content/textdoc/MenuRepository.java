package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Modifying
    @Query("DELETE FROM Menu m WHERE m.docVersion = ?1")
    void deleteByDocVersion(DocVersion docVersion);

    List<Menu> getByDocVersion(DocVersion docVersion);
}
