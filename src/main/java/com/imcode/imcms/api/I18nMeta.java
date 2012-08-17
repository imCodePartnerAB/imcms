package com.imcode.imcms.api;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Document i18n meta.
 */
@Entity
@Table(name = "imcms_doc_i18n_meta")
public class I18nMeta implements Serializable, Cloneable {

    public static class Builder {
        private I18nMeta i18nMeta = new I18nMeta();

        {
            i18nMeta.headline = "";
            i18nMeta.menuText = "";
            i18nMeta.menuImageURL = "";
        }

        public Builder() {}

        public Builder(I18nMeta i18nMeta) {
            id(i18nMeta.getId());
            docId(i18nMeta.getDocId());
            language(i18nMeta.getLanguage());
            headline(i18nMeta.getHeadline());
            menuText(i18nMeta.getMenuText());
            menuImageURL(i18nMeta.getMenuImageURL());
        }

        public I18nMeta build() {
            I18nMeta newI18nMeta = new I18nMeta();

            newI18nMeta.id = i18nMeta.id;
            newI18nMeta.docId = i18nMeta.docId;
            newI18nMeta.language = i18nMeta.language;
            newI18nMeta.headline = i18nMeta.headline;
            newI18nMeta.menuText = i18nMeta.menuText;
            newI18nMeta.menuImageURL = i18nMeta.menuImageURL;

            return newI18nMeta;
        }

        public Builder id(Integer id) {
            i18nMeta.id = id;
            return this;
        }

        public Builder headline(String headline) {
            i18nMeta.headline = headline;
            return this;
        }

        public Builder docId(Integer docId) {
            i18nMeta.docId = docId;
            return this;
        }

        public Builder language(I18nLanguage language) {
            i18nMeta.language = language;
            return this;
        }

        public Builder menuImageURL(String menuImageURL) {
            i18nMeta.menuImageURL = menuImageURL;
            return this;
        }

        public Builder menuText(String menuText) {
            i18nMeta.menuText = menuText;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(I18nMeta i18nMeta) {
        return new Builder(i18nMeta);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id")
    private Integer docId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private I18nLanguage language;

    /**
     * Doc's headline label. Mainly used as HTML page title.
     */
    @Column(name = "headline")
    private String headline;

    /**
     * Menu item label.
     * Used when a doc is included in other doc's menu (as a menu item).
     */
    @Column(name = "menu_text")
    private String menuText;

    /**
     * Menu item image.
     */
    @Column(name = "menu_image_url")
    private String menuImageURL;

    protected I18nMeta() {
    }

    public Integer getDocId() {
        return docId;
    }

    public String getHeadline() {
        return headline;
    }

    public Integer getId() {
        return id;
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public String getMenuImageURL() {
        return menuImageURL;
    }

    public String getMenuText() {
        return menuText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        I18nMeta i18nMeta = (I18nMeta) o;

        if (docId != null ? !docId.equals(i18nMeta.docId) : i18nMeta.docId != null) return false;
        if (headline != null ? !headline.equals(i18nMeta.headline) : i18nMeta.headline != null) return false;
        if (id != null ? !id.equals(i18nMeta.id) : i18nMeta.id != null) return false;
        if (language != null ? !language.equals(i18nMeta.language) : i18nMeta.language != null) return false;
        if (menuImageURL != null ? !menuImageURL.equals(i18nMeta.menuImageURL) : i18nMeta.menuImageURL != null)
            return false;
        if (menuText != null ? !menuText.equals(i18nMeta.menuText) : i18nMeta.menuText != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (docId != null ? docId.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (headline != null ? headline.hashCode() : 0);
        result = 31 * result + (menuText != null ? menuText.hashCode() : 0);
        result = 31 * result + (menuImageURL != null ? menuImageURL.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "I18nMeta{" +
                "docId=" + docId +
                ", id=" + id +
                ", language=" + language +
                ", headline='" + headline + '\'' +
                ", menuText='" + menuText + '\'' +
                ", menuImageURL='" + menuImageURL + '\'' +
                '}';
    }

    @Override
    public I18nMeta clone() {
        try {
            return (I18nMeta)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}