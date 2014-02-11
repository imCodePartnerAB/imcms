package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public final class DocumentAppearance implements Serializable {

    public static class Builder {
        private String headline;
        private String menuText;
        private String menuImageURL;

        public Builder() {
        }

        public Builder(DocumentAppearance documentAppearance) {
            this.headline = documentAppearance.headline;
            this.menuText = documentAppearance.menuText;
            this.menuImageURL = documentAppearance.menuImageURL;
        }

        public DocumentAppearance build() {
            return new DocumentAppearance(headline, menuText, menuImageURL);
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
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DocumentAppearance documentAppearance) {
        return new Builder(documentAppearance);
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
     * Menu item image.
     */
    private final String menuImageURL;

    public DocumentAppearance(String headline, String menuText, String menuImageURL) {
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DocumentAppearance && equals((DocumentAppearance) o));
    }

    private boolean equals(DocumentAppearance that) {
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
}