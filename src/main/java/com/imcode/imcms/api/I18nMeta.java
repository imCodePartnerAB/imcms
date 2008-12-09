package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * I18n-ed part of meta.
 */
@Entity
@Table(name="i18n_meta")
@NamedQueries({
	@NamedQuery(name="I18nMeta.getByLanguage", query="select m from I18nMeta m where m.metaId = :metaId and m.language.id = :languageId")
	//@NamedQuery(name="I18nMeta.getByMetaId&LanguageId", query="select m from I18nMeta m where m.metaId = :metaId and m.language.id=:languageId")
})
public class I18nMeta implements Serializable, Cloneable {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i18n_meta_id")
	private Long id;
	
	@Column(name="meta_id")
	private Long metaId;
	
	@Column(name="meta_enabled")
	private Boolean enabled;
				
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="language_id")
	private I18nLanguage language;
			
	@org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
	@javax.persistence.JoinTable(
		name = "keywords",
		joinColumns={
		    @JoinColumn(name="meta_id", referencedColumnName="meta_id"),
		    @JoinColumn(name="language_id", referencedColumnName="language_id")
		}
	)
	@Column(name = "value")
	private Set<String> keywords = new HashSet<String>();	
    
	@Column(name="meta_headline")
    private String headline;
    
	@Column(name="meta_text")
    private String menuText;
    
	@Column(name="meta_image")
    private String menuImageURL;
	
	@Override
	public I18nMeta clone() {
		try {
			I18nMeta clone = (I18nMeta)super.clone();
			
			if (keywords != null) {
				clone.keywords = new HashSet<String>(keywords);
			}		
			
			if (language != null) {
				clone.language = language.clone();
			}
			
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
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

	public Long getMetaId() {
		return metaId;
	}

	public void setMetaId(Long metaId) {
		this.metaId = metaId;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}	
}