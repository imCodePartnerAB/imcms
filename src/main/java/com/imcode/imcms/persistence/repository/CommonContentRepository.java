package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommonContentRepository extends JpaRepository<CommonContentJPA, Integer>, VersionedContentRepository<CommonContentJPA> {

    List<CommonContentJPA> findByDocIdAndVersionNo(int docId, int versionNo);

	@Query("SELECT c FROM CommonContentJPA c\n" +
			"WHERE c.docId IN (?1)\n" +
			"  AND c.language IN (?2)\n" +
			"  AND c.versionNo = (SELECT max(ic.versionNo) FROM CommonContentJPA ic WHERE ic.docId = c.docId)")
	List<CommonContentJPA> findByDocIdsAndLangsAndLatestVersion(Collection<Integer> docIds, List<LanguageJPA> languages);

    @Modifying
    void deleteByDocId(int docId);

    CommonContentJPA findByDocIdAndVersionNoAndLanguage(int docId, int versionNo, LanguageJPA language);

    CommonContentJPA findByDocIdAndVersionNoAndLanguageCode(int docId, int versionNo, String code);

	Optional<CommonContentJPA> findFirstByAlias(String alias);

	@Query(value = "SELECT * FROM imcms_doc_i18n_meta t1\n" +
			"         INNER JOIN (SELECT doc_id, MAX(version_no) AS max_version\n" +
			"                     FROM imcms_doc_i18n_meta\n" +
			"                     GROUP BY doc_id) t2 ON t1.doc_id = t2.doc_id AND\n" +
			"                                            (t1.version_no = t2.max_version OR t1.version_no = 0) AND\n" +
			"                                            alias = :#{#alias}", nativeQuery = true)
	List<CommonContentJPA> findByAliasAndMaxWorkingVersion(@Param("alias") String alias);

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

	@Modifying
	@Query("DELETE FROM CommonContentJPA t WHERE t.docId = :#{#version.docId} and t.versionNo = :#{#version.no}")
	void deleteByVersion(@Param("version") Version version);
}
