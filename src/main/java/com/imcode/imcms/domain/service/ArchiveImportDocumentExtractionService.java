package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImportProgress;
import org.springframework.web.multipart.MultipartFile;

public interface ArchiveImportDocumentExtractionService {

	void extract(MultipartFile file);

	ImportProgress getProgress();
}
