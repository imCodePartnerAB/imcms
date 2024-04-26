package com.imcode.imcms.domain.dto;

import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.ImageFileIndex;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ImageFilePageRequestDTO extends PageRequestDTO {

	public ImageFilePageRequestDTO() {
		super(ImageFileIndex.FIELD__UPLOADED, PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE_FOR_UI), Sort.Direction.DESC);
	}

	public ImageFilePageRequestDTO(int defaultPageSize) {
		super(ImageFileIndex.FIELD__UPLOADED, PageRequest.of(DEFAULT_PAGE_NUMBER, defaultPageSize), Sort.Direction.DESC);
	}

	public ImageFilePageRequestDTO(String property, Sort.Direction direction, int skip, int size) {
		super(property, PageRequest.of(DEFAULT_PAGE_NUMBER, size, Sort.by(direction, property)), direction, skip);
	}

}
