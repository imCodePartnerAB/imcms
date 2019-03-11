package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonContentRepository extends JpaRepository<CommonContentJPA, Integer>, VersionedContentRepository<CommonContentJPA> {

    List<CommonContentJPA> findByDocIdAndVersionNo(int docId, int versionNo);

    @Modifying
    void deleteByDocId(int docId);

    CommonContentJPA findByDocIdAndVersionNoAndLanguage(int docId, int versionNo, LanguageJPA language);

    CommonContentJPA findByDocIdAndVersionNoAndLanguageCode(int docId, int versionNo, String code);

    @Override
    @Query("select t from CommonContentJPA t where t.docId = :#{#version.docId} and t.versionNo = :#{#version.no}")
    List<CommonContentJPA> findByVersion(@Param("version") Version version);

}
