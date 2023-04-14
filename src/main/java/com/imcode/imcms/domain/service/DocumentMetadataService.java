package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentMetadataDTO;

import java.util.List;

public interface DocumentMetadataService {
	List<DocumentMetadataDTO> getDocumentMetadataList(Integer docId, String languageCode);

	List<DocumentMetadataDTO> getDocumentMetadataList(Integer docId, int versionNo, String languageCode);

	List<DocumentMetadataDTO> getPublicDocumentMetadataList(Integer docId, String languageCode);
}
