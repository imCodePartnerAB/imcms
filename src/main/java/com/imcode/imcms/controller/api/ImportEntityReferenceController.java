package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/import/references/")
public class ImportEntityReferenceController {
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	@GetMapping
	public List<ImportEntityReferenceDTO> getAllReferencesByType(@RequestParam String type) {
		return importEntityReferenceManagerService.getAllReferencesByType(ImportEntityReferenceType.valueOf(type));
	}

	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateReference(@RequestBody ImportEntityReferenceDTO importEntityReference) {
		importEntityReferenceManagerService.updateReference(importEntityReference);
	}

}
