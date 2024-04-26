package com.imcode.imcms.domain.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public abstract class PageRequestDTO {
	public static final int DEFAULT_PAGE_SIZE_FOR_UI = Integer.MAX_VALUE;
	protected static final int DEFAULT_PAGE_NUMBER = 0;

	protected PageRequest pageRequest;

	protected Sort.Direction direction;
	protected String property;

	protected int skip;

	public PageRequestDTO(String property, PageRequest pageRequest, Sort.Direction direction) {
		this.property = property;
		this.pageRequest = pageRequest;
		this.direction = direction;
	}

	public PageRequestDTO(String property, PageRequest pageRequest, Sort.Direction direction, int skip) {
		this.property = property;
		this.pageRequest = pageRequest;
		this.direction = direction;
		this.skip = skip;
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
