package com.imcode.imcms.api.orm;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.imcode.imcms.api.Meta;

@Entity
@Table(name="meta")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@org.hibernate.annotations.DiscriminatorFormula(
  "case when doc_type = 7 then 'OrmHtmlDocument' when doc_type = 5 then 'OrmUrlDocument' when doc_type = 2 then 'OrmTextDocument' when doc_type = 8 then 'OrmFileDocument'  else 'OrmDocument' end"
)
public class OrmDocument {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="meta_id")
	private Integer metaId;
	
	@Embedded
	@Basic(fetch=FetchType.EAGER)
	private Meta meta;

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer id) {
		this.metaId = id;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}	
}