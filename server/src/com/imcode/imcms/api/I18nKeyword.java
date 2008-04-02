package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="i18n_keywords")
public class I18nKeyword implements Serializable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="keyword_id")
	private Long id;
	
	@Column(name="i18n_meta_id")
	private Long i18nMetaId;	
	
	@Column(name="keyword_value")
	private String value;
	
	public I18nKeyword() {}
	
	public I18nKeyword(String value) {
		setValue(value);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getI18nMetaId() {
		return i18nMetaId;
	}

	public void setI18nMetaId(Long metaId) {
		i18nMetaId = metaId;
	}
}
