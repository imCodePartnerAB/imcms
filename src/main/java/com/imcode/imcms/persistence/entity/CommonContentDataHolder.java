package com.imcode.imcms.persistence.entity;

public interface CommonContentDataHolder<L> {
    Integer getId();

    void setId(Integer id);

    Integer getDocId();

    void setDocId(Integer docId);

    String getHeadline();

    void setHeadline(String headline);

    L getLanguage();

    void setLanguage(L language);

    String getMenuText();

    void setMenuText(String menuText);

    String getMenuImageURL();

    void setMenuImageURL(String menuImageURL);

    boolean isEnabled();

    void setEnabled(boolean isEnabled);

    Integer getVersionNo();

    void setVersionNo(Integer versionNo);

}
