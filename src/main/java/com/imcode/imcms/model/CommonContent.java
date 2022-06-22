package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class CommonContent implements Serializable {

    private static final long serialVersionUID = 4011970230207348148L;

    protected CommonContent(CommonContent from) {
	    setId(from.getId());
	    setDocId(from.getDocId());
	    setAlias(from.getAlias());
	    setHeadline(from.getHeadline());
	    setLanguage(from.getLanguage());
	    setMenuText(from.getMenuText());
	    setEnabled(from.isEnabled());
	    setVersionNo(from.getVersionNo());
    }

	public abstract Integer getId();

	public abstract void setId(Integer id);

	public abstract Integer getDocId();

	public abstract void setDocId(Integer docId);

	public abstract String getAlias();

	public abstract void setAlias(String alias);

	public abstract String getHeadline();

	public abstract void setHeadline(String headline);

	public abstract Language getLanguage();

	public abstract void setLanguage(Language language);

	public abstract String getMenuText();

	public abstract void setMenuText(String menuText);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean isEnabled);

    public abstract Integer getVersionNo();

    public abstract void setVersionNo(Integer versionNo);

}
