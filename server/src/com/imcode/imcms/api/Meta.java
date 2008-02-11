package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
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

@Entity
@Table(name="meta")
public class Meta implements Serializable {
	
	public static enum MissingI18nShowRule {
		SHOW_IN_DEFAULT_LANGUAGE,
		DO_NOT_SHOW
	}

	@Id
	@Column(name="meta_id")
	private Integer metaId;
	
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinColumn(name="meta_id", referencedColumnName="meta_id")
	private List<I18nMeta> i18nParts;
	
	@Enumerated(EnumType.STRING)
	@Column(name="missing_i18n_show_rule")
	private MissingI18nShowRule missingI18nShowRule;
	
	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public List<I18nMeta> getI18nParts() {
		return i18nParts;
	}

	public void setI18nParts(List<I18nMeta> i18nParts) {
		this.i18nParts = i18nParts;
	}

	public MissingI18nShowRule getMissingI18nShowRule() {
		return missingI18nShowRule;
	}

	public void setMissingI18nShowRule(MissingI18nShowRule missingI18nShowRule) {
		this.missingI18nShowRule = missingI18nShowRule;
	}
}
