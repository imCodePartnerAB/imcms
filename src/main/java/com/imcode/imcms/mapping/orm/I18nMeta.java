package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Document i18n meta.
 */
@Entity
@Table(name = "imcms_doc_i18n_meta")
public class I18nMeta implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id")
    private Integer docId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private DocumentLanguage language;

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

    public Integer getDocId() {
        return docId;
    }

    public String getHeadline() {
        return headline;
    }

    public Integer getId() {
        return id;
    }

    public DocumentLanguage getLanguage() {
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
        return o == this || (o instanceof I18nMeta && equals((I18nMeta) o));
    }

    private boolean equals(I18nMeta that) {
        return Objects.equals(id, that.id)
                && Objects.equals(docId, that.docId)
                && Objects.equals(headline, that.headline)
                && Objects.equals(menuText, that.menuText)
                && Objects.equals(menuImageURL, that.menuImageURL)
                && Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, docId, headline, menuText, menuImageURL, language);
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