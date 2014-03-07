package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.Language;

import javax.persistence.*;
import java.util.Objects;

/**
 * Document common content.
 */
@Entity
@Table(name = "imcms_doc_i18n_meta")
public class CommonContent {

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id")
    private Integer docId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private Language language;

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

    public CommonContent() {
    }

    public CommonContent(Integer docId, Language language, String headline, String menuText, String menuImageURL) {
        this.docId = docId;
        this.language = language;
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof CommonContent && equals((CommonContent) o));
    }

    private boolean equals(CommonContent that) {
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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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