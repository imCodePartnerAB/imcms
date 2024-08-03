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

    @Override
    @Query("select t from CommonContentJPA t where t.docId = :#{#version.docId} and t.versionNo = :#{#version.no}")
    List<CommonContentJPA> findByVersion(@Param("version") Version version);

	@Query("select t from CommonContentJPA t where t.docId = :#{#version.docId} and t.versionNo = :#{#version.no} and t.language = :#{#language}")
	CommonContentJPA findByVersionAndLanguage(@Param("version") Version version, @Param("language") LanguageJPA language);

	@Query(nativeQuery = true, value =
			"SELECT t1.* FROM imcms_doc_i18n_meta t1 " +
					"INNER JOIN (SELECT doc_id, MAX(version_no) AS max_version FROM imcms_doc_i18n_meta GROUP BY doc_id) t2 " +
					"ON alias = :#{#alias} AND t1.doc_id = t2.doc_id AND (t1.version_no = t2.max_version OR t1.version_no = 0)")
	List<CommonContentJPA> findByAliasAndLatestAndWorkingVersions(@Param("alias") String alias);

	@Query(nativeQuery = true, value =
			"SELECT t1.* FROM imcms_doc_i18n_meta t1 " +
					"INNER JOIN (SELECT doc_id, MAX(version_no) AS max_version FROM imcms_doc_i18n_meta GROUP BY doc_id) t2 " +
					"ON alias = :#{#alias} AND t1.doc_id = t2.doc_id AND t1.version_no = t2.max_version AND t1.version_no != 0")
	Optional<CommonContentJPA> findByAliasAndLatestVersion(@Param("alias") String alias);

	@Query(nativeQuery = true, value =
			"SELECT CASE WHEN COUNT(*) > 0 THEN 'true' ELSE 'false' END AS result FROM imcms_doc_i18n_meta t1 " +
					"INNER JOIN (SELECT doc_id, MAX(version_no) AS max_version FROM imcms_doc_i18n_meta GROUP BY doc_id) t2 " +
					"ON alias = :#{#alias} AND t1.doc_id = t2.doc_id AND (t1.version_no = t2.max_version OR t1.version_no = 0)")
	Boolean existsByAliasAndLatestAndWorkingVersions(@Param("alias") String alias);

	@Query(nativeQuery = true, value =
			"SELECT CASE WHEN COUNT(*) > 0 THEN 'true' ELSE 'false' END AS result FROM imcms_doc_i18n_meta t1 " +
					"INNER JOIN (SELECT doc_id, MAX(version_no) AS max_version FROM imcms_doc_i18n_meta GROUP BY doc_id) t2 " +
					"ON alias = :#{#alias} AND t1.doc_id = t2.doc_id AND t1.version_no = t2.max_version AND t1.version_no != 0")
	Boolean existsByAliasAndLatestVersion(@Param("alias") String alias);

	@Query(nativeQuery = true, value =
			"SELECT t1.doc_id FROM imcms_doc_i18n_meta t1 " +
					"INNER JOIN (SELECT doc_id, MAX(version_no) AS max_version FROM imcms_doc_i18n_meta GROUP BY doc_id) t2 " +
					"ON alias = :#{#alias} AND t1.doc_id = t2.doc_id AND t1.version_no = t2.max_version AND t1.version_no != 0")
	Optional<Integer> getDocIdByAliasAndLatestVersion(@Param("alias") String alias);

	@Query(nativeQuery = true, value =
			"SELECT DISTINCT LOWER(t1.alias) FROM imcms_doc_i18n_meta t1 " +
					"INNER JOIN (SELECT doc_id, MAX(version_no) AS max_version FROM imcms_doc_i18n_meta GROUP BY doc_id) t2 " +
					"ON t1.doc_id = t2.doc_id AND (t1.version_no = t2.max_version OR t1.version_no = 0)")
	List<String> findAllAliasesByLatestAndWorkingVersions();

	@Modifying
	@Query("update CommonContentJPA set alias = null where alias=:alias")
	void removeAlias(@Param("alias") String alias);

	@Modifying
	@Query("DELETE FROM CommonContentJPA t WHERE t.docId = :#{#version.docId} and t.versionNo = :#{#version.no}")
	void deleteByVersion(@Param("version") Version version);
}
