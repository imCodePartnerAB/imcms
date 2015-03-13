package com.imcode.imcms.api;

/**
 * Created by Shadowgun on 06.03.2015.
 */
public class PagerItem {
    private String link;
    private Integer pageNumber;
    private boolean isShowed;

    public PagerItem(String link, Integer pageNumber, boolean isShowed) {
        this.link = link;
        this.pageNumber = pageNumber;
        this.isShowed = isShowed;
    }

    public String getLink() {
        return link;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public boolean isShowed() {
        return isShowed;
    }
}
