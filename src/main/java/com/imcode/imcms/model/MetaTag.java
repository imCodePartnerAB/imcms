package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p> HTML meta tag property/name and their description <p/>
 */
@NoArgsConstructor
public abstract class MetaTag implements Serializable {

	@Serial
	private static final long serialVersionUID = 2264975307106484000L;

	protected MetaTag(MetaTag from) {
		setId(from.getId());
		setName(from.getName());
	}

	public abstract Integer getId();

	public abstract void setId(Integer id);

	/**
	 * @return name of meta tag extension
	 */
	public abstract String getName();

	public abstract void setName(String name);
}
