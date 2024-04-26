package com.imcode.imcms.domain.dto;

import com.imcode.imcms.domain.service.MenuService;
import imcode.server.document.index.DocumentIndex;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class DocumentPageRequestDTO extends PageRequestDTO {

	public DocumentPageRequestDTO() {
		super(DocumentIndex.FIELD__MODIFIED_DATETIME, PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE_FOR_UI),
				Sort.Direction.DESC);
	}

	public DocumentPageRequestDTO(int defaultPageSize) {
		super(DocumentIndex.FIELD__MODIFIED_DATETIME, PageRequest.of(DEFAULT_PAGE_NUMBER, defaultPageSize),
				Sort.Direction.DESC);
	}

	public DocumentPageRequestDTO(String property, Sort.Direction direction, int skip, int size) {
		super(property, PageRequest.of(DEFAULT_PAGE_NUMBER, size, Sort.by(direction, property)), direction, skip);
	}
}
