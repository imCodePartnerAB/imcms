package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="i18n_languages")
@NamedQueries({
	@NamedQuery(name="I18nLanguage.getDefaultLanguage", query="select l from I18nLanguage l where l.default is true"),
	@NamedQuery(name="I18nLanguage.getByCode", query="select l from I18nLanguage l where l.code = :code"),
	@NamedQuery(name="I18nLanguage.missingMetaLanguages", query="select l from I18nLanguage l where l not in (select m.language from I18nMeta m where m.metaId = :metaId)")
})
public class I18nLanguage implements Serializable, Cloneable {


    private Integer id;
	
	private Boolean systemDefault;
	
	private Boolean enabled;	
    
    private String code;
    
    private String name;
    
    private String nativeName;

	@Override
	public boolean equals(Object object) {
		return object instanceof I18nLanguage
			&& hashCode() == ((I18nLanguage) object).hashCode();
	}

	@Override
	public I18nLanguage clone() {
		try {
			return (I18nLanguage)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}	
	}
	
	@Override	
	public int hashCode() {
		return id;
	}
	
	@Override 
	public String toString() {
		return getName();
	}
	
	@Id
	@Column(name="language_id")	
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

	@Column(name="native_name")	
	public String getNativeName() {
		return nativeName;
	}
	
	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

	@Column(name="is_default")	
	public Boolean isDefault() {
		return systemDefault;
	}

	public void setDefault(Boolean systemDefault) {
		this.systemDefault = systemDefault;
	}

	@Column(name="is_enabled")	
	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}	
}