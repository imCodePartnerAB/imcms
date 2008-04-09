package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="meta")
/**
 * Document meta.
 */
public class Meta implements Serializable, Cloneable {
	
	// TODO i18n: refactor
    // public static enum UnavailableI18nDataSubstitution {
    //     DEFAULT_LANGUAGE_DATA,
    //     EMPTY_DATA
	// }
	
	/**
	 * Substitution modes for disabled or missing i18n-ed data.
	 * 
	 * Data considered unavailable if inner accessor returns null.
	 * Note: 
	 *   ImageDomainObject instance also considered unavailable if its URL property is not set
	 *   TextDomainObject instance also considered unavailable if its text property is empty or null   
	 */
	public static enum UnavailableI18nDataSubstitution {
		SHOW_IN_DEFAULT_LANGUAGE,
		DO_NOT_SHOW,		
	}
	
	@Transient
	private Map<I18nLanguage, I18nMeta> metaMap;

	@Id
	@Column(name="meta_id")
	private Integer metaId;
	
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinColumn(name="meta_id", referencedColumnName="meta_id")		
	private List<I18nMeta> i18nMetas;
	
	@Enumerated(EnumType.STRING)
	@Column(name="missing_i18n_show_rule")
	private UnavailableI18nDataSubstitution unavailableI18nDataSubstitution;
	
	@Override
	public Meta clone() {
		try {
			Meta clone = (Meta)super.clone();
			
			if (i18nMetas != null) {
				clone.i18nMetas = new LinkedList<I18nMeta>();
			
				for (I18nMeta i18nMeta: i18nMetas) {
					clone.i18nMetas.add(i18nMeta.clone());	
				}
			}
			
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}	
	
	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public List<I18nMeta> getI18nMetas() {
		return i18nMetas;
	}
	
	public void setI18nMetas(List<I18nMeta> i18nParts) {
		this.i18nMetas = i18nParts;		
	}

	public UnavailableI18nDataSubstitution getUnavailableI18nDataSubstitution() {
		return unavailableI18nDataSubstitution;
	}

	public void setUnavailableI18nDataSubstitution(UnavailableI18nDataSubstitution unavailableI18nDataSubstitution) {
		this.unavailableI18nDataSubstitution = unavailableI18nDataSubstitution;
	}
	
	// TODO i18n : refactor
	public synchronized I18nMeta getI18nMeta(I18nLanguage language) {
		if (metaMap == null) {
			metaMap = new HashMap<I18nLanguage, I18nMeta>();
			
			if (i18nMetas != null) {
				for (I18nMeta i18nMeta: i18nMetas) {
					metaMap.put(i18nMeta.getLanguage(), i18nMeta);
				}
			}			
		}
		
		return metaMap.get(language);
	}
}
