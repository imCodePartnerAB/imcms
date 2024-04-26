package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ImageFileStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchImageFileQueryDTO;
import com.imcode.imcms.domain.service.SearchImageFileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/images/files/search")
public class SearchImageFileController {

	private final SearchImageFileService searchImageFileService;

	public SearchImageFileController(SearchImageFileService searchImageFileService) {
		this.searchImageFileService = searchImageFileService;
	}

	@GetMapping
	public List<ImageFileStoredFieldsDTO> search(SearchImageFileQueryDTO queryDTO) {
		return searchImageFileService.search(queryDTO);
	}
}
