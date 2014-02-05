package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Objects;

/**
 * Document i18n meta.
 */
public class I18nMeta implements Serializable {

    public static class Builder {
        private String headline;
        private String menuText;
        private String menuImageURL;
        private DocumentLanguage language;

        public Builder() {
        }

        public Builder(I18nMeta i18nMeta) {
            this.headline = i18nMeta.headline;
            this.menuText = i18nMeta.menuText;
            this.menuImageURL = i18nMeta.menuImageURL;
            this.language = i18nMeta.language;
        }

        public I18nMeta build() {
            return new I18nMeta(headline, menuText, menuImageURL, language);
        }


        public Builder headline(String headline) {
            this.headline = headline;
            return this;
        }

        public Builder language(DocumentLanguage language) {
            this.language = language;
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

    public static Builder builder(I18nMeta i18nMeta) {
        return new Builder(i18nMeta);
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

    private final DocumentLanguage language;

    public I18nMeta(String headline, String menuText, String menuImageURL, DocumentLanguage language) {
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof I18nMeta && equals((I18nMeta) o));
    }

    private boolean equals(I18nMeta that) {
        return Objects.equals(headline, that.headline)
                && Objects.equals(menuText, that.menuText)
                && Objects.equals(menuImageURL, that.menuImageURL)
                && Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headline, menuText, menuImageURL, language);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("headline", headline)
                .add("menuText", menuText)
                .add("menuImageUrl", menuImageURL)
                .add("language", language)
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

    public DocumentLanguage getLanguage() {
        return language;
    }
}