package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.util.Objects;

/**
 * Document i18n meta.
 */
@Entity(name = "I18nMeta")
@Table(name = "imcms_doc_i18n_meta")
public class DocI18nMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id")
    private Integer docId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private DocLanguage language;

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

    public DocLanguage getLanguage() {
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
        return o == this || (o instanceof DocI18nMeta && equals((DocI18nMeta) o));
    }

    private boolean equals(DocI18nMeta that) {
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
}