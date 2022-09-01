package com.imcode.imcms.mapping;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Content common to all document types.
 */
public final class DocumentCommonContent implements Serializable {

	private static final long serialVersionUID = -3790331598505829889L;
	private final String alias;
	/**
	 * Doc's headline label. Mainly used as HTML page title.
	 */
	private final String headline;
	/**
	 * Menu item label.
	 * Used when a doc is included in other doc's menu (as a menu item).
	 */
	private final String menuText;
	/**
	 * Is current language enabled
	 */
	private final boolean enabled;
	/**
	 * Version of document
	 */
	private final Integer versionNo;

	public DocumentCommonContent() {
		this("", "", "", true, 0);
	}

	public DocumentCommonContent(String alias, String headline, String menuText, boolean enabled,
	                             Integer versionNo) {
		this.alias = alias;
		this.headline = headline;
		this.menuText = menuText;
		this.enabled = enabled;
		this.versionNo = versionNo;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(DocumentCommonContent documentCommonContent) {
		return new Builder(documentCommonContent);
	}

	@Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DocumentCommonContent && equals((DocumentCommonContent) o));
    }

    private boolean equals(DocumentCommonContent that) {
        return Objects.equals(headline, that.headline)
                && Objects.equals(menuText, that.menuText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headline, menuText);
    }

    @Override
    public String toString() {
	    return MoreObjects.toStringHelper(this)
			    .add("alias", alias)
			    .add("headline", headline)
			    .add("menuText", menuText)
			    .add("enabled", enabled)
			    .add("versionNo", versionNo)
			    .toString();
    }

	public String getAlias() {
		return alias;
	}

	public String getHeadline() {
		return headline;
	}

	public String getMenuText() {
		return menuText;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public static class Builder {
		private String alias;
		private String headline;
		private String menuText;
		private boolean enabled = true;
		private Integer versionNo;

		public Builder() {
		}

		public Builder(DocumentCommonContent documentCommonContent) {
			this.alias = documentCommonContent.alias;
			this.headline = documentCommonContent.headline;
			this.menuText = documentCommonContent.menuText;
			this.enabled = documentCommonContent.enabled;
			this.versionNo = documentCommonContent.versionNo;
		}

		public DocumentCommonContent build() {
			return new DocumentCommonContent(alias, headline, menuText, enabled, versionNo);
		}

		public Builder alias(String alias) {
			this.alias = alias;
			return this;
		}

		public Builder headline(String headline) {
			this.headline = headline;
			return this;
		}

		public Builder menuText(String menuText) {
			this.menuText = menuText;
			return this;
		}

		public Builder enabled(Boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder versionNo(Integer versionNo) {
			this.versionNo = versionNo;
			return this;
		}
    }
}