package com.imcode.imcms.mapping.jpa.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version, Integer>, VersionRepositoryCustom {

    @Query("SELECT v FROM Version v WHERE v.docId = ?1 ORDER BY v.no")
    List<Version> findByDocId(int docId);

//    @Query("SELECT v FROM Version v WHERE v.docId = ?1 AND v.no = ?2")
    Version findByDocIdAndNo(int docId, int no);

    @Query("SELECT v FROM Version v WHERE v.no = 0 AND v.docId = ?1")
    Version findWorking(int docId);

    @Query("SELECT v FROM Meta m, Version v WHERE m.defaultVersionNo = v.no AND m.id = v.docId AND m.id = ?1")
    Version findDefault(int docId);

    @Query("SELECT v FROM Version v WHERE v.id = (SELECT max(v.id) FROM Version v WHERE v.docId = ?1)")
    Version findLatest(int docId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v.no FROM Version v WHERE v.id = (SELECT max(v.id) FROM Version v WHERE v.docId = ?1)")
    Integer findLatestNoForUpdate(int docId);

    @Modifying
    @Query("UPDATE Meta m SET m.defaultVersionNo = :no, m.publisherId = :publisherId WHERE m.id = :docId")
    void updateDefaultNo(@Param("docId") int docId,
                         @Param("no") int no,
                         @Param("publisherId") int userId);
}

