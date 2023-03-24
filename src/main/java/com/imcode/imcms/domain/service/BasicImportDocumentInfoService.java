package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.model.ImportDocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BasicImportDocumentInfoService {
	BasicImportDocumentInfoDTO create(int id, ImportDocumentStatus status);

	BasicImportDocumentInfoDTO save(BasicImportDocumentInfoDTO basicImportDocument);

	List<BasicImportDocumentInfoDTO> save(List<BasicImportDocumentInfoDTO> basicImportDocuments);

	Optional<BasicImportDocumentInfoDTO> getById(Integer id);

	Page<BasicImportDocumentInfoDTO> getAll();

	Page<BasicImportDocumentInfoDTO> getAll(Integer startId, Integer endId, boolean excludeImported, boolean excludeSkip, Pageable pageable);

	boolean exists(int importDocId);

	boolean imported(int importDocId);

	int toMetaId(int importDocId);
}
