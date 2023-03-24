package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import com.imcode.imcms.model.ImportDocumentStatus;
import com.imcode.imcms.persistence.entity.BasicImportDocumentInfoJPA;
import com.imcode.imcms.persistence.repository.BasicImportDocumentInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DefaultBasicImportDocumentInfoService implements BasicImportDocumentInfoService {
	private final BasicImportDocumentInfoRepository basicImportDocumentInfoRepository;

	@Override
	public BasicImportDocumentInfoDTO create(int id, ImportDocumentStatus status) {
		if (basicImportDocumentInfoRepository.existsById(id)) {
			throw new RuntimeException("Basic Import Document already exists id=" + id);
		}

		return new BasicImportDocumentInfoDTO(basicImportDocumentInfoRepository.save(new BasicImportDocumentInfoJPA(id, status)));
	}

	@Override
	public BasicImportDocumentInfoDTO save(BasicImportDocumentInfoDTO basicImportDocument) {
		return saveBasicImportDocument(basicImportDocument);
	}

	@Override
	public List<BasicImportDocumentInfoDTO> save(List<BasicImportDocumentInfoDTO> basicImportDocuments) {
		return basicImportDocuments
				.stream()
				.map(this::saveBasicImportDocument)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<BasicImportDocumentInfoDTO> getById(Integer id) {
		return basicImportDocumentInfoRepository.findById(id)
				.map(BasicImportDocumentInfoDTO::new);
	}

	@Override
	public Page<BasicImportDocumentInfoDTO> getAll() {
		return getAll(null, null, false, false, Pageable.unpaged());
	}

	@Override
	public Page<BasicImportDocumentInfoDTO> getAll(Integer startId, Integer endId, boolean excludeImported, boolean excludeSkip, Pageable pageable) {
		return basicImportDocumentInfoRepository.findAllWithRange(startId, endId, excludeImported, excludeSkip, pageable)
				.map(BasicImportDocumentInfoDTO::new);
	}

	@Override
	public boolean exists(int importDocId) {
		return basicImportDocumentInfoRepository.existsById(importDocId);
	}

	@Override
	public boolean imported(int importDocId) {
		return basicImportDocumentInfoRepository.isImported(importDocId);
	}

	@Override
	public int toMetaId(int importDocId) {
		return basicImportDocumentInfoRepository.findMetaId(importDocId);
	}

	private BasicImportDocumentInfoDTO saveBasicImportDocument(BasicImportDocumentInfoDTO basicImportDocument) {
		return new BasicImportDocumentInfoDTO(basicImportDocumentInfoRepository.save(new BasicImportDocumentInfoJPA(basicImportDocument)));
	}
}
