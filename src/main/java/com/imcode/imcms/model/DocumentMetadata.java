package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p> Describes HTML meta tag content <p>
 *
 * @see MetaTag
 */
@NoArgsConstructor
public abstract class DocumentMetadata implements Serializable {
	@Serial
	private static final long serialVersionUID = 2264975307106484000L;

	public DocumentMetadata(DocumentMetadata from) {
		setMetaTag(from.getMetaTag());
		setContent(from.getContent());
	}

	public abstract MetaTag getMetaTag();

	public abstract void setMetaTag(MetaTag metaTag);

	/**
	 * @return meta tag content
	 */
	public abstract String getContent();

	public abstract void setContent(String content);
}
