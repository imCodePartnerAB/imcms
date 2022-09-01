package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentMetadata;
import com.imcode.imcms.model.MetaTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DocumentMetadataDTO extends DocumentMetadata {
	@Serial
	private static final long serialVersionUID = 3074958637096727597L;
	private MetaTagDTO metaTag;
	private String content;

	public DocumentMetadataDTO(DocumentMetadata from) {
		super(from);
	}

	@Override
	public void setMetaTag(MetaTag metaTag) {
		this.metaTag = (metaTag == null) ? null : new MetaTagDTO(metaTag);
	}
}
