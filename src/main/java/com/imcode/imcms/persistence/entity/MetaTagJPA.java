package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.MetaTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serial;

@Data
@Entity
@Table(name = "imcms_html_meta_tags")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MetaTagJPA extends MetaTag {

	@Serial
	private static final long serialVersionUID = -5205516167784663525L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	public MetaTagJPA(MetaTag from) {
		super(from);
	}

	public MetaTagJPA(String metaTagName) {
		this.name = metaTagName;
	}
}
