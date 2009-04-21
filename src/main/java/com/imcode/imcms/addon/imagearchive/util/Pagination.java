package com.imcode.imcms.addon.imagearchive.util;

import java.io.Serializable;

public class Pagination implements Serializable{
    private static final long serialVersionUID = -7220000190165261133L;
    
    private int pageSize = 1;
    private int startPosition;
    private int currentPage;
    private int pageCount;
    private boolean hasNextPage;
    private boolean hasPrevPage;
    
    
    public Pagination() {
    }
    
    public Pagination(int pageSize) {
        this.pageSize = pageSize;
    }
    
    
    public static Pagination create(int pageSize, int currentPage, int elementCount) {
        Pagination pagination = new Pagination(pageSize);
        
        pagination.setCurrentPage(currentPage);
        pagination.update(elementCount);
        
        return pagination;
    }
    
    public void update(int elementCount) {
        pageCount = ((elementCount - 1) / pageSize) + 1;
        
        currentPage = Math.min(currentPage, pageCount - 1);
        currentPage = Math.max(currentPage, 0);
        
        startPosition = currentPage * pageSize;
        
        startPosition = Math.min(startPosition, elementCount - 1);
        startPosition = Math.max(startPosition, 0);
        
        hasNextPage = (currentPage < (pageCount - 1));
        hasPrevPage = (currentPage > 0);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public int getPageCount() {
        return pageCount;
    }
}
