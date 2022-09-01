package com.imcode.imcms.domain.dto;

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
public class MetaTagDTO extends MetaTag {

	@Serial
	private static final long serialVersionUID = 3074958637096727597L;

	private Integer id;
	private String name;

	public MetaTagDTO(MetaTag from) {
		super(from);
	}
}
