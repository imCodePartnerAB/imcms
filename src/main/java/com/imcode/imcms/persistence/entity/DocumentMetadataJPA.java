package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.DocumentMetadata;
import com.imcode.imcms.model.MetaTag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serial;

@Data
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DocumentMetadataJPA extends DocumentMetadata {
	@Serial
	private static final long serialVersionUID = -1880722894965996448L;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_tag_id", referencedColumnName = "id")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private MetaTagJPA metaTag;

	@Column(name = "content", length = 2048)
	private String content;

	public DocumentMetadataJPA(DocumentMetadata from) {
		super(from);
	}

	@Override
	public void setMetaTag(MetaTag metaTag) {
		this.metaTag = (metaTag == null) ? null : new MetaTagJPA(metaTag);
	}
}
