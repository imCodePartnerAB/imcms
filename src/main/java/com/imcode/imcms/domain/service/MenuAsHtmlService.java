package com.imcode.imcms.domain.service;

public interface MenuAsHtmlService {

    String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                String attributes, String treeKey, String wrap);

    String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                               String attributes, String treeKey, String wrap);

    String getVisibleMenuAsHtml(int docId, int menuIndex);

    String getPublicMenuAsHtml(int docId, int menuIndex);

}
