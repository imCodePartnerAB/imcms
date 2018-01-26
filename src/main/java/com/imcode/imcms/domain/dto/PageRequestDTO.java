package com.imcode.imcms.domain.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestDTO {

    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    private PageRequest pageRequest;

    public PageRequestDTO() {
        this.pageRequest = new PageRequest(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
    }

    public PageRequestDTO(int page, int size) {
        this.pageRequest = new PageRequest(page, size);
    }

    public PageRequestDTO(int page, int size, Sort sort) {
        this.pageRequest = new PageRequest(page, size, sort);
    }

    public int getPage() {
        return pageRequest.getPageNumber();
    }

    public void setPage(int page) {
        pageRequest = new PageRequest(page, pageRequest.getPageSize(), pageRequest.getSort());
    }

    public int getSize() {
        return pageRequest.getPageSize();
    }

    public void setSize(int size) {
        pageRequest = new PageRequest(pageRequest.getPageNumber(), size, pageRequest.getSort());
    }

    public Sort getSort() {
        return pageRequest.getSort();
    }
}
