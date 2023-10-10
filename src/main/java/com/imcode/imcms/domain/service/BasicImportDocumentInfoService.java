package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.model.ImportDocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BasicImportDocumentInfoService extends DeleterByDocumentId {
	BasicImportDocumentInfoDTO create(int id, ImportDocumentStatus status);

	BasicImportDocumentInfoDTO save(BasicImportDocumentInfoDTO basicImportDocument);

	List<BasicImportDocumentInfoDTO> save(List<BasicImportDocumentInfoDTO> basicImportDocuments);

	Optional<BasicImportDocumentInfoDTO> getById(Integer id);

	Page<BasicImportDocumentInfoDTO> getAll();

	Page<BasicImportDocumentInfoDTO> getAll(Set<Integer> docIdList);

	Page<BasicImportDocumentInfoDTO> getAll(Set<Integer> docIdList, Pageable pageable);

	Page<BasicImportDocumentInfoDTO> getAllInRange(Integer startId, Integer endId);

	Page<BasicImportDocumentInfoDTO> getAllInRange(Integer startId, Integer endId, boolean excludeImported, boolean excludeSkip);

	Page<BasicImportDocumentInfoDTO> getAllInRange(Integer startId, Integer endId, boolean excludeImported, boolean excludeSkip, Pageable pageable);

	boolean exists(int importDocId);

	boolean isImported(int importDocId);

	Optional<Integer> toMetaId(int importDocId);
}
