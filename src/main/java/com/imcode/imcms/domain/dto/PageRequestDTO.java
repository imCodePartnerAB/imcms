package com.imcode.imcms.domain.dto;

import imcode.server.document.index.DocumentIndex;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestDTO {

    public static final int DEFAULT_PAGE_SIZE_FOR_UI = Integer.MAX_VALUE;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    private PageRequest pageRequest;

    private Sort.Direction direction;
    private String property;

    private int skip;

    public PageRequestDTO() {
	    this.pageRequest = PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE_FOR_UI);
	    this.property = DocumentIndex.FIELD__MODIFIED_DATETIME;
	    this.direction = Sort.Direction.DESC;
    }

    public PageRequestDTO(int defaultPageSize) {
	    this.pageRequest = PageRequest.of(DEFAULT_PAGE_NUMBER, defaultPageSize);
	    this.property = DocumentIndex.FIELD__MODIFIED_DATETIME;
	    this.direction = Sort.Direction.DESC;
    }

    public PageRequestDTO(String property, Sort.Direction direction, int skip, int size) {
	    this.property = property;
	    this.direction = direction;
	    this.skip = skip;

	    final Sort sort = Sort.by(direction, property);
	    this.pageRequest = PageRequest.of(DEFAULT_PAGE_NUMBER, size, sort);
    }

    public int getSize() {
        return pageRequest.getPageSize();
    }

    public void setSize(int size) {
	    pageRequest = PageRequest.of(pageRequest.getPageNumber(), size, pageRequest.getSort());
    }

    public Sort getSort() {
        return pageRequest.getSort();
    }

    private void setSort() {
	    final Sort sort = Sort.by(this.direction, this.property);
	    pageRequest = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
        setSort();
    }

    public void setProperty(String property) {
        this.property = property;
        setSort();
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }
}
