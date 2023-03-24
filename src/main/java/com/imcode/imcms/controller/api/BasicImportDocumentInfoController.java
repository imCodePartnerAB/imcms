package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/import/info")
public class BasicImportDocumentInfoController {
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;

	@GetMapping("all")
	public Page<BasicImportDocumentInfoDTO> getAllBasicImportDocuments(Pageable pageable,
	                                                                   @RequestParam(required = false) Integer startId,
	                                                                   @RequestParam(required = false) Integer endId,
	                                                                   @RequestParam(required = false, defaultValue = "false") boolean excludeImported,
	                                                                   @RequestParam(required = false, defaultValue = "false") boolean excludeSkip) {

		return basicImportDocumentInfoService.getAll(startId, endId, excludeImported, excludeSkip, pageable);
	}

	@PostMapping
	public void updateBasicImportDocument(@RequestBody BasicImportDocumentInfoDTO basicImportDocumentInfo) {
		basicImportDocumentInfoService.save(basicImportDocumentInfo);
	}

}
