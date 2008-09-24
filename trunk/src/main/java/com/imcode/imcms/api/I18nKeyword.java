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
public class I18nKeyword implements Serializable, Cloneable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="keyword_id")
	private Long id;
	
	@Column(name="keyword_value")
	private String value;
	
	public I18nKeyword() {}
	
	public I18nKeyword(String value) {
		setValue(value);
	}
	
	@Override
	public I18nKeyword clone() {
		try {
			return (I18nKeyword)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}	
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
}
