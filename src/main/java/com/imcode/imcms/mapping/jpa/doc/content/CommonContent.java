package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.persistence.entity.Language;

import javax.persistence.*;
import java.util.Objects;

/**
 * Content common to all document types.
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

    /**
     * Flag indicates is current language enabled for document
     */
    @Column(name = "is_enabled")
    private boolean isEnabled;

    /**
     * Related document version number
     */
    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    public CommonContent() {
    }

    /**
     * @deprecated use {@link #CommonContent(Integer, Language, String, String, String, Boolean, Integer)}
     */
    @Deprecated
    public CommonContent(Integer docId, Language language, String headline, String menuText, String menuImageURL) {
        this.docId = docId;
        this.language = language;
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
    }

    public CommonContent(Integer docId, Language language, String headline, String menuText, String menuImageURL,
                         Boolean isEnabled, Integer versionNo) {
        this.docId = docId;
        this.language = language;
        this.headline = headline;
        this.menuText = menuText;
        this.menuImageURL = menuImageURL;
        this.isEnabled = isEnabled;
        this.versionNo = versionNo;
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

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }
}