package com.imcode.imcms.mapping;

import java.io.Serializable;
import java.util.Objects;

/**
 * Content common to all document types.
 */
public final class DocumentCommonContent implements Serializable {

    public static class Builder {
        private String headline;
        private String menuText;
        private String menuImageURL;
        private Boolean enabled;

        public Builder() {
        }

        public Builder(DocumentCommonContent documentCommonContent) {
            this.headline = documentCommonContent.headline;
            this.menuText = documentCommonContent.menuText;
            this.menuImageURL = documentCommonContent.menuImageURL;
            this.enabled = documentCommonContent.enabled;
        }

        public DocumentCommonContent build() {
            return new DocumentCommonContent(headline, menuText, menuImageURL, enabled);
        }


        public Builder headline(String headline) {
            this.headline = headline;
            return this;
        }

        public Builder menuImageURL(String menuImageURL) {
            this.menuImageURL = menuImageURL;
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
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DocumentCommonContent documentCommonContent) {
        return new Builder(documentCommonContent);
    }

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
     * Menu item image url.
     */
    private final String menuImageURL;

    /**
     * Is current language enabled
     */
    private final Boolean enabled;

    public DocumentCommonContent() {
        this("", "", "", null);
    }

    public DocumentCommonContent(String headline, String menuText, String menuImageURL, Boolean enabled) {
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DocumentCommonContent && equals((DocumentCommonContent) o));
    }

    private boolean equals(DocumentCommonContent that) {
        return Objects.equals(headline, that.headline)
                && Objects.equals(menuText, that.menuText)
                && Objects.equals(menuImageURL, that.menuImageURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headline, menuText, menuImageURL);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("headline", headline)
                .add("menuText", menuText)
                .add("menuImageUrl", menuImageURL)
                .add("enabled", enabled)
                .toString();
    }

    public String getHeadline() {
        return headline;
    }

    public String getMenuText() {
        return menuText;
    }

    public String getMenuImageURL() {
        return menuImageURL;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}