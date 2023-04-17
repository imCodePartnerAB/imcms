package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DocumentWasteBasketJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Integer> {

    @Query("SELECT m.id FROM Meta m")
    List<Integer> findAllIds();

    /**
     * Finds all document ids that lies between (inclusively).
     *
     * @param from start id, including it
     * @param to   end id, including it
     * @return a {@code List} of found ids
     */
    @Query("SELECT m.id FROM Meta m WHERE m.id BETWEEN ?1 and ?2")
    List<Integer> findIdsBetween(int from, int to);

    @Query("SELECT min(m.id) FROM Meta m")
    Integer findMinId();

    @Query("SELECT max(m.id) FROM Meta m")
    Integer findMaxId();

    @Query("SELECT min(m.id), max(m.id) FROM Meta m")
    Integer[] findMinAndMaxId();

    @Query("SELECT m.target FROM Meta m WHERE m.id = ?1")
    String findTarget(int docId);

    @Query("select m.documentType from Meta m where m.id = ?1")
    DocumentType findType(Integer docId);

    @Query("select m.documentWasteBasket from Meta m where m.id = ?1")
    DocumentWasteBasketJPA findWasteBasket(Integer docId);
}
