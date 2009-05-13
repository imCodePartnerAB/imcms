package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name="i18n_languages")
public class I18nLanguage implements Serializable, Cloneable {


    private Integer id;
	
	private Boolean systemDefault;
	
	private Boolean enabled;	
    
    private String code;
    
    private String name;
    
    private String nativeName;

	@Override
	public boolean equals(Object object) {
        if ( !( object instanceof I18nLanguage ) ) {
            return false;
        }
        
        if (this == object) {
        	return true;
        }
        
        I18nLanguage that = (I18nLanguage)object;
        
        return new EqualsBuilder()
			.append(code, that.code).isEquals();
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
        return new HashCodeBuilder(11, 31)
			.append(code).toHashCode();
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