package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="i18n_languages_v")
@NamedQueries({
	@NamedQuery(name="I18nLanguage.getDefaultLanguage", query="select l from I18nLanguage l where l.systemDefault is true"),
	@NamedQuery(name="I18nLanguage.getByCode", query="select l from I18nLanguage l where l.code = :code"),
	@NamedQuery(name="I18nLanguage.missingMetaLanguages", query="select l from I18nLanguage l where l not in (select m.language from I18nMetaPart m where m.metaId = :metaId)")
})
public class I18nLanguage implements Serializable {

	@Id
	@Column(name="language_id")	
    private Integer id;
	
	@Column(name="system_default")
	private Boolean systemDefault;
    
	@Column(name="language_code_iso_639_1")	
    private String code;
    
	@Column(name="language_name")
    private String name;
    
	@Column(name="language_native_name")
    private String nativeName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNativeName() {
		return nativeName;
	}

	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

	public Boolean getSystemDefault() {
		return systemDefault;
	}

	public void setSystemDefault(Boolean systemDefault) {
		this.systemDefault = systemDefault;
	}	
}
