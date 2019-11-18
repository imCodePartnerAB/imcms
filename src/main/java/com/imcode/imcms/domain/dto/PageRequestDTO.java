package com.imcode.imcms.domain.dto;

import imcode.server.document.index.DocumentIndex;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestDTO {

    private static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    private PageRequest pageRequest;

    private Sort.Direction direction;
    private String property;

    private int skip;

    public PageRequestDTO() {
        this.pageRequest = new PageRequest(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
        this.property = DocumentIndex.FIELD__META_ID;
        this.direction = Sort.Direction.DESC;
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

    private void setSort() {
        final Sort sort = new Sort(new Sort.Order(this.direction, this.property));
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
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
