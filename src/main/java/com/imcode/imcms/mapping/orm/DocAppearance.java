package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.util.Objects;

/**
 * Document appearance.
 */
@Entity(name = "Appearance")
@Table(name = "imcms_doc_i18n_meta")
public class DocAppearance {

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

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DocAppearance && equals((DocAppearance) o));
    }

    private boolean equals(DocAppearance that) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public DocLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocLanguage language) {
        this.language = language;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getMenuText() {
        return menuText;
    }

    public void setMenuText(String menuText) {
        this.menuText = menuText;
    }

    public String getMenuImageURL() {
        return menuImageURL;
    }

    public void setMenuImageURL(String menuImageURL) {
        this.menuImageURL = menuImageURL;
    }
}