package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImportProgress;
import org.springframework.web.multipart.MultipartFile;

public interface ImportDocumentService {
	boolean isExtractingDone();

	void extractAndSave(MultipartFile file);

	ImportProgress getExtractionProgress();

	boolean isImportingDone();

	void importDocuments(int startId, int endId);

	ImportProgress getImportingProgress();

	void removeAliases(int startId, int endId);
	void removeAlias(int importDocId);

	void replaceAliases(int startId, int endId);

}
