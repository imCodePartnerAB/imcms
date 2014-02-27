package com.imcode.imcms.mapping;

import java.io.Serializable;
import java.util.Objects;

/**
 * Content common to all document types.
 */
public final class CommonContentVO implements Serializable {

    public static class Builder {
        private String headline;
        private String menuText;
        private String menuImageURL;

        public Builder() {
        }

        public Builder(CommonContentVO commonContentVO) {
            this.headline = commonContentVO.headline;
            this.menuText = commonContentVO.menuText;
            this.menuImageURL = commonContentVO.menuImageURL;
        }

        public CommonContentVO build() {
            return new CommonContentVO(headline, menuText, menuImageURL);
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

    public static Builder builder(CommonContentVO commonContentVO) {
        return new Builder(commonContentVO);
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

    public CommonContentVO() {
        this("", "", "");
    }

    public CommonContentVO(String headline, String menuText, String menuImageURL) {
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof CommonContentVO && equals((CommonContentVO) o));
    }

    private boolean equals(CommonContentVO that) {
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