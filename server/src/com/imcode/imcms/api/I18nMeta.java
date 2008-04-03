package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * I18n-ed part of meta.
 */
@Entity
@Table(name="i18n_meta")
@NamedQueries({
	@NamedQuery(name="I18nMeta.getByLanguage", query="select m from I18nMeta m where m.metaId = :metaId and m.language.id = :languageId")
	//@NamedQuery(name="I18nMeta.getByMetaId&LanguageId", query="select m from I18nMeta m where m.metaId = :metaId and m.language.id=:languageId")
})
public class I18nMeta implements Serializable {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i18n_meta_id")
	private Long id;
	
	@Column(name="meta_id")
	private Integer metaId;
	
	@Column(name="meta_enabled")
	private Boolean enabled;
				
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="language_id")
	private I18nLanguage language;
		
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@JoinColumn(name="i18n_meta_id", referencedColumnName="i18n_meta_id")
	private Set<I18nKeyword> keywords;
    
	@Column(name="meta_headline")
    private String headline;
    
	@Column(name="meta_text")
    private String menuText;
    
	@Column(name="meta_image")
    private String menuImageURL;

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

	public String getMenuImageURL() {
		return menuImageURL;
	}

	public void setMenuImageURL(String image) {
		this.menuImageURL = image;
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
		
	public Set<String> getKeywordsValues() {
    	Set<String> values = new HashSet<String>();
    	
    	for (I18nKeyword keyword: keywords) {
    		values.add(keyword.getValue());
    	}
	
    	return values;
	}
	
	public void setKeywordsValues(Set<String> values) {
		keywords.clear();
		
		for (String value: values) {
			I18nKeyword keyword = new I18nKeyword(value);
			
			keywords.add(keyword);
		}
	}
}