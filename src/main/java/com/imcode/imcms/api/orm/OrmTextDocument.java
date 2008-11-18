package com.imcode.imcms.api.orm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.TemplateNames;
import imcode.server.document.textdocument.TextDomainObject;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

@Entity
@SecondaryTable(name="text_docs", pkJoinColumns = {@PrimaryKeyJoinColumn(name="meta_id")})
public class OrmTextDocument extends OrmDocument {
	
	@Embedded
	@Basic(fetch=FetchType.EAGER)
	@AttributeOverrides({
		@AttributeOverride(name="templateName", column=@Column(name="template_name", table="text_docs")),
		@AttributeOverride(name="templateGroupId", column=@Column(name="group_id", table="text_docs")),
		@AttributeOverride(name="defaultTemplateName", column=@Column(name="default_template", table="text_docs")),
		@AttributeOverride(name="defaultTemplateNameForRestricted1", column=@Column(name="default_template_1", table="text_docs")),
		@AttributeOverride(name="defaultTemplateNameForRestricted2", column=@Column(name="default_template_2", table="text_docs"))})	
	private TemplateNames templateNames;
	
	@org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "includes",
	    joinColumns = @JoinColumn(name="meta_id"))	
	@org.hibernate.annotations.MapKey(
	   columns = @Column(name="include_id"))
	@Column(name = "included_meta_id")   
    private Map<Integer, Integer> includesMap = new HashMap<Integer, Integer>();
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="meta_id")
	private Set<TextDomainObject> texts = new HashSet<TextDomainObject>();
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="meta_id")
	private Set<ImageDomainObject> images = new HashSet<ImageDomainObject>();	
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="meta_id")
	private Map<Integer, MenuDomainObject> menus = new HashMap<Integer, MenuDomainObject>();	
	
	public TemplateNames getTemplateNames() {
		return templateNames;
	}

	public void setTemplateNames(TemplateNames templateNames) {
		this.templateNames = templateNames;
	}

	public Map<Integer, Integer> getIncludesMap() {
		return includesMap;
	}

	public void setIncludesMap(Map<Integer, Integer> includesMap) {
		this.includesMap = includesMap;
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
}