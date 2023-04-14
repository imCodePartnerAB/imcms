package com.imcode.imcms.domain.service;

public interface MenuAsHtmlService {

    String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                String attributes, String treeKey, String wrap);

    String getVisibleMenuAsHtml(int docId, int menuIndex, int versionNo, String language,
                                String attributes, String treeKey, String wrap);

    String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                               String attributes, String treeKey, String wrap);

    String getVisibleMenuAsHtml(int docId, int menuIndex);

    String getVisibleMenuAsHtml(int docId, int menuIndex, int versionNo);

    String getPublicMenuAsHtml(int docId, int menuIndex);

}
