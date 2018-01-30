package com.imcode.imcms.domain.dto;

import imcode.server.document.index.DocumentIndex;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestDTO {

    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    private PageRequest pageRequest;

    private Sort.Direction direction;
    private String property;

    public PageRequestDTO() {
        this.pageRequest = new PageRequest(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

        property = DocumentIndex.FIELD__META_ID;
        direction = Sort.Direction.DESC;
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

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
        setSort();
    }

    public void setProperty(String property) {
        this.property = property;
        setSort();
    }

    private void setSort() {
        final Sort sort = new Sort(new Sort.Order(this.direction, this.property));
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
    }
}
