package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/import/info")
public class BasicImportDocumentInfoController {
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;

	@GetMapping("all")
	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	public Page<BasicImportDocumentInfoDTO> getAllBasicImportDocuments(Pageable pageable,
																	   @RequestParam(required = false) Integer startId,
																	   @RequestParam(required = false) Integer endId,
																	   @RequestParam(required = false) Set<Integer> docIdList,
																	   @RequestParam(required = false, defaultValue = "false") boolean excludeImported,
																	   @RequestParam(required = false, defaultValue = "false") boolean excludeSkip) {

		if (docIdList != null) {
			return basicImportDocumentInfoService.getAll(docIdList, pageable);
		}

		return basicImportDocumentInfoService.getAllInRange(startId, endId, excludeImported, excludeSkip, pageable);
	}

	@PostMapping
	@CheckAccess(role = AccessRoleType.ADMIN_PAGES)
	public void updateBasicImportDocument(@RequestBody BasicImportDocumentInfoDTO basicImportDocumentInfo) {
		basicImportDocumentInfoService.save(basicImportDocumentInfo);
	}

}
