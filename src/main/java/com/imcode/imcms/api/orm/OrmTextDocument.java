package com.imcode.imcms.api.orm;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.TemplateNames;
import imcode.server.document.textdocument.TextDomainObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class OrmTextDocument extends OrmDocument {
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="meta_id")
	private TemplateNames templateNames;
	
	@OneToMany(fetch=FetchType.EAGER)  
	@JoinColumn(name="meta_id")
    private Set<OrmInclude> includes = new HashSet<OrmInclude>();
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="meta_id")
	private Set<TextDomainObject> texts = new HashSet<TextDomainObject>();
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="meta_id")
	private Set<ImageDomainObject> images = new HashSet<ImageDomainObject>();	
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="meta_id")
	@MapKey(name="index")
	private Map<Integer, MenuDomainObject> menus = new HashMap<Integer, MenuDomainObject>();	
	
	public TemplateNames getTemplateNames() {
		return templateNames;
	}

	public void setTemplateNames(TemplateNames templateNames) {
		this.templateNames = templateNames;
	}

	public Set<TextDomainObject> getTexts() {
		return texts;
	}

	public void setTexts(Set<TextDomainObject> texts) {
		this.texts = texts;
	}

	public Set<ImageDomainObject> getImages() {
		return images;
	}

	public void setImages(Set<ImageDomainObject> images) {
		this.images = images;
	}

	public Map<Integer, MenuDomainObject> getMenus() {
		return menus;
	}

	public void setMenus(Map<Integer, MenuDomainObject> menus) {
		this.menus = menus;
	}

	public Set<OrmInclude> getIncludes() {
		return includes;
	}

	public void setIncludes(Set<OrmInclude> includes) {
		this.includes = includes;
	}
}