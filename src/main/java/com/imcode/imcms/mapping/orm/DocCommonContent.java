package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.util.Objects;

/**
 * Document appearance.
 */
@Entity(name = "Appearance")
@Table(name = "imcms_doc_i18n_meta")
public class DocCommonContent {

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id")
    private Integer docId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private DocLanguage docLanguage;

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

    public DocCommonContent() {
    }

    public DocCommonContent(Integer docId, DocLanguage docLanguage, String headline, String menuText, String menuImageURL) {
        this.docId = docId;
        this.docLanguage = docLanguage;
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DocCommonContent && equals((DocCommonContent) o));
    }

    private boolean equals(DocCommonContent that) {
        return Objects.equals(id, that.id)
                && Objects.equals(docId, that.docId)
                && Objects.equals(headline, that.headline)
                && Objects.equals(menuText, that.menuText)
                && Objects.equals(menuImageURL, that.menuImageURL)
                && Objects.equals(docLanguage, that.docLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, docId, headline, menuText, menuImageURL, docLanguage);
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

    public DocLanguage getDocLanguage() {
        return docLanguage;
    }

    public void setDocLanguage(DocLanguage language) {
        this.docLanguage = language;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline == null ? null : headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
    }

    public String getMenuText() {
        return menuText;
    }

    public void setMenuText(String menuText) {
        this.menuText = menuText == null ? null : menuText.substring(0, Math.min(menuText.length(), META_TEXT_MAX_LENGTH - 1));
    }

    public String getMenuImageURL() {
        return menuImageURL;
    }

    public void setMenuImageURL(String menuImageURL) {
        this.menuImageURL = menuImageURL;
    }
}