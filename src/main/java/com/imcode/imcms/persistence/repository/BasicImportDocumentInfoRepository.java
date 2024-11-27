package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.BasicImportDocumentInfoJPA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BasicImportDocumentInfoRepository extends JpaRepository<BasicImportDocumentInfoJPA, Integer> {
	@Query("select b from BasicImportDocumentInfoJPA b " +
			"where (:startId is null or b.id >= :startId) " +
			"and (:endId is null or b.id <= :endId) " +
			"and (:excludeImported is false or b.status <> 'IMPORTED') " +
			"and (:excludeSkip is false or b.status <> 'SKIP') ")
	Page<BasicImportDocumentInfoJPA> findAllWithRange(@Param("startId") Integer startId,
													  @Param("endId") Integer endId,
													  @Param("excludeImported") boolean excludeImported,
													  @Param("excludeSkip") boolean excludeSkip,
													  Pageable pageable);

	Page<BasicImportDocumentInfoJPA> findAllByIdIn(Set<Integer> docIdList, Pageable pageable);

	@Query("select case when (count(b) > 0) then true else false end " +
			"from BasicImportDocumentInfoJPA b " +
			"where b.id=:id and lower(b.status)=lower('Imported')")
	boolean isImported(@Param("id") Integer id);

	@Query("select b from BasicImportDocumentInfoJPA b " +
			"where (b.id >= :startId and b.id <= :endId) and lower(b.status)=lower('Imported')")
	List<BasicImportDocumentInfoJPA> findAllImportedByIdRange(@Param("startId") int startId, @Param("endId") int endId);

	@Query("select b from BasicImportDocumentInfoJPA b " +
			"where b.id IN (:ids) and lower(b.status)=lower('Imported')")
	List<BasicImportDocumentInfoJPA> findAllImportedByIds(@Param("ids") int[] ids);

	@Query("select b.metaId from BasicImportDocumentInfoJPA b where b.id=:importDocId")
	Optional<Integer> findMetaId(@Param("importDocId") Integer importDocId);

	void deleteByMetaId(Integer docIdToDelete);
}
