package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class CommonContent<Lang extends Language> {

    protected <CommonContent1 extends CommonContent> CommonContent(CommonContent1 from, Lang language) {
        setId(from.getId());
        setDocId(from.getDocId());
        setHeadline(from.getHeadline());
        setLanguage(language);
        setMenuText(from.getMenuText());
        setMenuImageURL(from.getMenuImageURL());
        setEnabled(from.isEnabled());
        setVersionNo(from.getVersionNo());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Integer getDocId();

    public abstract void setDocId(Integer docId);

    public abstract String getHeadline();

    public abstract void setHeadline(String headline);

    public abstract Lang getLanguage();

    public abstract void setLanguage(Lang language);

    public abstract String getMenuText();

    public abstract void setMenuText(String menuText);

    public abstract String getMenuImageURL();

    public abstract void setMenuImageURL(String menuImageURL);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean isEnabled);

    public abstract Integer getVersionNo();

    public abstract void setVersionNo(Integer versionNo);

}
