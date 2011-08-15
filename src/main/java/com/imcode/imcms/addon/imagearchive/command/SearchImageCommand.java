package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;
import java.util.Date;

public class SearchImageCommand implements Serializable {
    private static final long serialVersionUID = 844191223450843364L;
    
    public static final short SHOW_ALL = 0;
    public static final short SHOW_NEW = 1;
    public static final short SHOW_ERASED = 2;
    public static final short SHOW_WITH_VALID_LICENCE = 3;
    
    public static final int CATEGORY_ALL = -1;
    public static final int CATEGORY_NO_CATEGORY = -2;
    public static final long KEYWORD_ALL = -1;
    
    public static final short SORT_BY_ARTIST = 0;
    public static final short SORT_BY_ALPHABET = 1;
    public static final short SORT_BY_ENTRY_DATE = 2;

    public static final short SORT_ASCENDING = 0;
    public static final short SORT_DESCENDING = 1;
    
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    private short show = SHOW_ALL;
    private int categoryId = CATEGORY_ALL;
    private long keywordId = KEYWORD_ALL;
    private String freetext;
    private String artist;
    private String licenseDt;
    private String licenseEndDt;
    private String activeDt;
    private String activeEndDt;
    private int resultsPerPage = DEFAULT_PAGE_SIZE;
    private short sortBy = SORT_BY_ARTIST;
    private short sortOrder = SORT_ASCENDING;
    private String clearAction;
    
    private Date licenseDate;
    private Date licenseEndDate;
    private Date activeDate;
    private Date activeEndDate;

    private boolean fileNamesOnly;
    private boolean isUnfolded;

    
    public SearchImageCommand() {
    }

    
    public void copyFrom(SearchImageCommand command) {
        this.show = command.getShow();
        this.categoryId = command.getCategoryId();
        this.keywordId = command.getKeywordId();
        this.freetext = command.getFreetext();
        this.artist = command.getArtist();
        this.licenseDt = command.getLicenseDt();
        this.licenseEndDt = command.getLicenseEndDt();
        this.activeDt = command.getActiveDt();
        this.activeEndDt = command.getActiveEndDt();
        this.resultsPerPage = command.getResultsPerPage();
        this.sortBy = command.getSortBy();
        this.sortOrder = command.getSortOrder();
        
        this.licenseDate = command.getLicenseDate();
        this.licenseEndDate = command.getLicenseEndDate();
        this.activeDate = command.getActiveDate();
        this.activeEndDate = command.getActiveEndDate();
        this.fileNamesOnly = command.isFileNamesOnly();
        this.isUnfolded = command.isUnfolded();
    }
    
    
    public boolean isClear() {
        return clearAction != null;
    }
    
    public String getActiveDt() {
        return activeDt;
    }

    public void setActiveDt(String activeDt) {
        this.activeDt = activeDt;
    }

    public String getActiveEndDt() {
        return activeEndDt;
    }

    public void setActiveEndDt(String activeEndDt) {
        this.activeEndDt = activeEndDt;
    }

    public String getFreetext() {
        return freetext;
    }

    public void setFreetext(String freetext) {
        this.freetext = freetext;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public short getShow() {
        return show;
    }

    public void setShow(short show) {
        this.show = show;
    }

    public short getSortBy() {
        return sortBy;
    }

    public void setSortBy(short sortBy) {
        this.sortBy = sortBy;
    }

    public String getLicenseDt() {
        return licenseDt;
    }

    public void setLicenseDt(String licenseDt) {
        this.licenseDt = licenseDt;
    }

    public String getLicenseEndDt() {
        return licenseEndDt;
    }

    public void setLicenseEndDt(String licenseEndDt) {
        this.licenseEndDt = licenseEndDt;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Date getActiveEndDate() {
        return activeEndDate;
    }

    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    public Date getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(Date licenseDate) {
        this.licenseDate = licenseDate;
    }

    public Date getLicenseEndDate() {
        return licenseEndDate;
    }

    public void setLicenseEndDate(Date licenseEndDate) {
        this.licenseEndDate = licenseEndDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getClearAction() {
        return clearAction;
    }

    public void setClearAction(String clearAction) {
        this.clearAction = clearAction;
    }

    public boolean isFileNamesOnly() {
        return fileNamesOnly;
    }

    public void setFileNamesOnly(boolean fileNamesOnly) {
        this.fileNamesOnly = fileNamesOnly;
    }

    public boolean isUnfolded() {
        return isUnfolded;
    }

    public void setUnfolded(boolean unfolded) {
        isUnfolded = unfolded;
    }

    public short getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(short sortOrder) {
        this.sortOrder = sortOrder;
    }

}
