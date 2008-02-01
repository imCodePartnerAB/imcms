package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * I18n-ed part of meta.
 */
@Entity
@Table(name="i18n_meta_part")
@NamedQueries({
	//@NamedQuery(name="I18nMeta.getMetaLanguageRightJoin", query="select l, m from I18nMeta m right join m.language l where m.metaId = :metaId"),
	//@NamedQuery(name="I18nMeta.getByMetaId&LanguageId", query="select m from I18nMeta m where m.metaId = :metaId and m.language.id=:languageId")
})
public class I18nMetaPart implements Serializable {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="part_id")
	private Long id;
	
	@Column(name="meta_id")
	private Integer metaId;
	
	@Column(name="meta_enabled")
	private Boolean enabled;
				
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="language_id", referencedColumnName="language_id")
	private I18nLanguage language;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinTable(name="i18n_meta_keywords", 
		joinColumns={@JoinColumn(name="meta_id")},			
		inverseJoinColumns={@JoinColumn(name="keyword_id")}
	)
	private Set<I18nKeyword> keywords;
    
	@Column(name="meta_headline")
    private String headline;
    
	@Column(name="meta_text")
    private String menuText;
    
	@Column(name="meta_image")
    private String imageURL;

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getMenuText() {
		return menuText;
	}

	public void setMenuText(String text) {
		this.menuText = text;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String image) {
		this.imageURL = image;
	}

	public I18nLanguage getLanguage() {
		return language;
	}

	public void setLanguage(I18nLanguage language) {
		this.language = language;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Set<I18nKeyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<I18nKeyword> keywords) {
		this.keywords = keywords;
	}
	
	// TODO ajosua: refactor out
	public String getKeywordsAsString() {
		if (keywords == null) {return "";};
		
		StringBuilder sb = new StringBuilder();
		
		for (I18nKeyword keyword: keywords) {
			sb.append(keyword.getValue());
			sb.append(",");
		}
		
		return sb.toString();
	}
}