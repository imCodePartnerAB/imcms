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

	CommonContentJPA findFirstByAlias(String alias);

    @Override
    @Query("select t from CommonContentJPA t where t.docId = :#{#version.docId} and t.versionNo = :#{#version.no}")
    List<CommonContentJPA> findByVersion(@Param("version") Version version);

	@Query("select t from CommonContentJPA t where t.docId = :#{#version.docId} and t.versionNo = :#{#version.no} and t.language = :#{#language}")
	CommonContentJPA findByVersionAndLanguage(@Param("version") Version version, @Param("language") LanguageJPA language);

	@Query("SELECT CASE WHEN count(c) > 0 THEN true ELSE false END FROM CommonContentJPA c WHERE c.alias = ?1")
	Boolean existsByAlias(String alias);

	@Query(value = "SELECT doc_id FROM imcms_doc_i18n_meta WHERE lower(alias) = lower(:#{#alias}) limit 1", nativeQuery = true)
	Integer findDocIdByAlias(@Param("alias") String alias);

	@Query("select distinct lower(c.alias) from CommonContentJPA c")
	List<String> findAllAliases();

	@Modifying
	@Query("update CommonContentJPA set alias = null where alias=:alias")
	void removeAlias(@Param("alias") String alias);
}
