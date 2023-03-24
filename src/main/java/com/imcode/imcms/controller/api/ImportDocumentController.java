package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImportProgress;
import com.imcode.imcms.domain.service.ImportDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/import")
public class ImportDocumentController {
	private final ImportDocumentService importDocumentService;

	@PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadDocumentsZip(@RequestParam("file") MultipartFile file) {
		final String zipFilename = file.getOriginalFilename();
		final String extension = FilenameUtils.getExtension(zipFilename);

		if (StringUtils.isEmpty(zipFilename) || !extension.equals("zip")) {
			log.error("Uploaded zip file has no name or has bad file type. Supports only .zip.");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		importDocumentService.extractAndSave(file);

		return zipFilename;
	}

	@GetMapping(value = "/upload/progress")
	public ImportProgress getUploadProgress() {
		return importDocumentService.getExtractionProgress();
	}


	@GetMapping(value = "/progress")
	public ImportProgress getImportProgress() {
		return importDocumentService.getImportingProgress();
	}

	@PostMapping
	public void importDocuments(@RequestBody Map<String, Integer> params) {
		final Integer startId = params.get("start");
		final Integer endId = params.get("end");

		if (startId == null || endId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide start and end ids!");
		}

		importDocumentService.importDocuments(startId, endId);
	}

	@PostMapping("/aliases/remove")
	public void removeAliases(@RequestBody Map<String, Integer> params) {
		final Integer startId = params.get("start");
		final Integer endId = params.get("end");

		if (startId == null || endId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide start and end ids!");
		}

		importDocumentService.removeAliases(startId, endId);
	}

	@PostMapping("/aliases/replace")
	public void replaceAliases(@RequestBody Map<String, Integer> params) {
		final Integer startId = params.get("start");
		final Integer endId = params.get("end");

		if (startId == null || endId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide start and end ids!");
		}

		importDocumentService.replaceAliases(startId, endId);
	}
}
