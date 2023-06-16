package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImportProgress;
import org.springframework.web.multipart.MultipartFile;

public interface ImportDocumentService {
	boolean isExtractingDone();

	void extractAndSave(MultipartFile file);

	ImportProgress getExtractionProgress();

	boolean isImportingDone();

	void importDocuments(int startId, int endId);
	void importDocuments(int[] metaIds);

	ImportProgress getImportingProgress();

	void removeAlias(int importDocId);

	void removeAliases(int[] importDocIds);

	void removeAliasesInRange(int startId, int endId);

	void replaceAlias(int importDocId);

	void replaceAliases(int[] importDocIds);

	void replaceAliasesInRange(int startId, int endId);

}
