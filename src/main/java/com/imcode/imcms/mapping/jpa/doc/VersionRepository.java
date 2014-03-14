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

    @Query(name = "Version.findByDocIdAndNoOrderByNo")
    List<Version> findByDocId(int docId);

    @Query(name = "Version.findByDocIdAndNo")
    Version findByDocIdAndNo(int docId, int no);

    @Query(name = "Version.findWorking")
    Version findWorking(int docId);

    @Query(name = "Version.findDefault")
    Version findDefault(int docId);

    @Query(name = "Version.findLatest")
    Version findLatest(int docId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(name = "Version.findLatestNo")
    Integer findLatestNoForUpdate(int docId);

    @Modifying
    @Query(name = "Version.updateDefaultNo")
    void updateDefaultNo(@Param("docId") int docId,
                         @Param("no") int no,
                         @Param("publisherId") int userId);
}

