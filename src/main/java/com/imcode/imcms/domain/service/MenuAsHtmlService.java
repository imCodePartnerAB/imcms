package com.imcode.imcms.domain.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MenuAsHtmlService {

    List<String> getMenuItemsAsHtmlByData(String dataClass);

    String getVisibleMenuAsHtml(int docId, int menuIndex,
                                String language, boolean nested,
                                String attributes, String treeKey, String wrap);

    String getPublicMenuAsHtml(int docId, int menuIndex,
                               String language, boolean nested,
                               String attributes, String treeKey, String wrap);

    String getVisibleMenuAsHtml(int docId, int menuIndex);

    String getPublicMenuAsHtml(int docId, int menuIndex);

}
