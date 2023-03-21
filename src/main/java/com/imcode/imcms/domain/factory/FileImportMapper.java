package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.ImportFileDTO;
import com.imcode.imcms.domain.service.DocumentFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileImportMapper {

	private final DocumentFileService documentFileService;

	public void mapAndSave(int metaId, ImportFileDTO importFile) {
		final DocumentFileDTO file = new DocumentFileDTO();

		file.setDocId(metaId);
		file.setFilename(importFile.getFilename());
		file.setMimeType(importFile.getMime());
		file.setDefaultFile(importFile.isDefault());

		documentFileService.save(file);
	}

	public void mapAndSave(int metaId, List<ImportFileDTO> importFiles) {
		importFiles.forEach(importFile -> mapAndSave(metaId, importFile));
	}
}
