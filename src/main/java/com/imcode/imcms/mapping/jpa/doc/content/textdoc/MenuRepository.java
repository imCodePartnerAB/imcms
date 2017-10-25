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

    @Query("SELECT m FROM Menu m WHERE m.version = ?1")
    List<Menu> findByVersion(Version version);

    @Query("SELECT m.id FROM Menu m WHERE m.version = ?1 AND m.no = ?2")
    Integer findIdByVersionAndNo(Version version, int no);

    @Query("SELECT m.version.docId, m.no FROM Menu m JOIN m.items i WHERE KEY(i) = ?1 AND m.version.no = ?2")
    List<Object[]> getParentDocumentAndMenuIdsForDocument(int linkedDocId, int versionNo);

    @Query("SELECT m.version.docId, m.no FROM Menu m JOIN m.items i WHERE KEY(i) = ?1 AND m.version.no = ?2 ORDER BY m.version.docId, m.no")
    List<Integer[]> getDocumentMenuPairsContainingDocument(int linkedDocId, int versionNo);

    @Query("SELECT m.version.docId FROM Menu m JOIN m.items i WHERE KEY(i) = ?1 AND m.version.no = ?2 ORDER BY m.version.docId, m.no")
    List<Integer> getParentDocsIds(int linkedDocId, int versionNo);

}
