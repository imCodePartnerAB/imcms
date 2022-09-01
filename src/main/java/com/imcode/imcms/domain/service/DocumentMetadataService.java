package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.DocumentMetadata;

import java.util.List;

public interface DocumentMetadataService {
	List<? extends DocumentMetadata> getDocumentMetadataList(Integer docId, String languageCode);

	List<? extends DocumentMetadata> getPublicDocumentMetadataList(Integer docId, String languageCode);
}
