package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImportProgress;
import com.imcode.imcms.domain.service.ArchiveImportDocumentExtractionService;
import com.imcode.imcms.domain.service.ImportDocumentService;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/import")
public class ImportDocumentController {
	private final ImportDocumentService importDocumentService;
	private final ArchiveImportDocumentExtractionService archiveImportDocumentExtractionService;

	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	@PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadDocumentsZip(@RequestParam("file") MultipartFile file) {
		final String zipFilename = file.getOriginalFilename();
		final String extension = FilenameUtils.getExtension(zipFilename);

		if (StringUtils.isEmpty(zipFilename) || !extension.equals("zip")) {
			log.error("Uploaded zip file has no name or has bad file type. Supports only .zip.");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		archiveImportDocumentExtractionService.extract(file);

		return zipFilename;
	}

	@GetMapping(value = "/upload/progress")
	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	public ImportProgress getUploadProgress() {
		return archiveImportDocumentExtractionService.getProgress();
	}


	@GetMapping(value = "/progress")
	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	public ImportProgress getImportProgress() {
		return importDocumentService.getImportingProgress();
	}

	@PostMapping
	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	public void importDocuments(@RequestBody ImportDocIdRequestDTO importDocIdRequestDTO) {
		if (importDocIdRequestDTO.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide meta ids: list or range");
		}

		final int[] metaIdList = importDocIdRequestDTO.getImportDocIds();
		if (metaIdList != null && metaIdList.length != 0) {
			importDocumentService.importDocuments(importDocIdRequestDTO.getImportDocIds(), importDocIdRequestDTO.isAutoImportMenus());
			return;
		}

		importDocumentService.importDocuments(importDocIdRequestDTO.getStartId(), importDocIdRequestDTO.getEndId(), importDocIdRequestDTO.isAutoImportMenus());
	}

	@PostMapping("/aliases/remove")
	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	public void removeAliases(@RequestBody ImportDocIdRequestDTO metaIdRequest) {
		if (metaIdRequest.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide meta ids: list or range");
		}

		final int[] metaIdList = metaIdRequest.getImportDocIds();
		if (metaIdList != null && metaIdList.length != 0) {
			importDocumentService.removeAliases(metaIdRequest.getImportDocIds());
			return;
		}

		importDocumentService.removeAliasesInRange(metaIdRequest.getStartId(), metaIdRequest.getEndId());
	}

	@PostMapping("/aliases/replace")
	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	public void replaceAliases(@RequestBody ImportDocIdRequestDTO metaIdRequest) {
		if (metaIdRequest.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide meta ids: list or range");
		}

		final int[] metaIdList = metaIdRequest.getImportDocIds();
		if (metaIdList != null && metaIdList.length != 0) {
			importDocumentService.replaceAliases(metaIdRequest.getImportDocIds());
			return;
		}

		importDocumentService.replaceAliasesInRange(metaIdRequest.getStartId(), metaIdRequest.getEndId());
	}

	@Data
	private static class ImportDocIdRequestDTO {
		private int[] importDocIds;
		private Integer startId;
		private Integer endId;
		private boolean autoImportMenus;

		public boolean isEmpty() {
			return (importDocIds == null || importDocIds.length == 0) && startId == null && endId == null;
		}
	}
}
