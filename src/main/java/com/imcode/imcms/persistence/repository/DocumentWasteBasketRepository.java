package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DocumentWasteBasketJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentWasteBasketRepository extends JpaRepository<DocumentWasteBasketJPA, Integer> {

    @Query("SELECT metaId FROM DocumentWasteBasketJPA ")
    List<Integer> findAllMetaIds();

    void deleteByMetaId(Integer metaId);

    void deleteByMetaIdIn(List<Integer> metaIds);

}
