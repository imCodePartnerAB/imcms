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
    List<Integer[]> getParentDocumentAndMenuIdsForDocument(int linkedDocId, int versionNo);
//    public List<Integer[]> getParentDocumentAndMenuIdsForDocument(int docId) {
//        return entityManager.createNativeQuery("SELECT doc_id, no FROM imcms_text_doc_menu_items childs, imcms_text_doc_menus menus WHERE menus.id = childs.menu_id AND to_doc_id=?")
//                .setParameter(1, docId)
//                .getResultList();
//    }

    @Query("SELECT m.version.docId, m.no FROM Menu m JOIN m.items i WHERE KEY(i) = ?1 AND m.version.no = ?2 ORDER BY m.version.docId, m.no")
    List<Integer[]> getDocumentMenuPairsContainingDocument(int linkedDocId, int versionNo);
//    public List<Integer[]>  getDocumentMenuPairsContainingDocument(int docId) {
//        return entityManager.createNativeQuery("SELECT doc_id, no FROM imcms_text_doc_menus menus, imcms_text_doc_menu_items childs WHERE menus.id = childs.menu_id AND childs.to_doc_id=? ORDER BY doc_id,no")
//                .setParameter(1, docId)
//                .getResultList();

    @Query("SELECT m.version.docId FROM Menu m JOIN m.items i WHERE KEY(i) = ?1 AND m.version.no = ?2 ORDER BY m.version.docId, m.no")
    List<Integer> getParentDocsIds(int linkedDocId, int versionNo);
//    public List<Integer> getParentDocsIds(int docId) {
//        return entityManager.createNativeQuery("SELECT doc_id FROM imcms_text_doc_menus menus, imcms_text_doc_menu_items childs WHERE menus.id = childs.menu_id AND childs.to_doc_id = ? ORDER BY doc_id, no")
//                .setParameter(1, docId)
//                .getResultList();
//
//    }

}
