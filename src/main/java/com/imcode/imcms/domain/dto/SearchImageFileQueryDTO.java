package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class SearchImageFileQueryDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 8802994228436485574L;

	private String term;

	private ImageFilePageRequestDTO page;

	public SearchImageFileQueryDTO(String term) {
		this.term = term;
	}
}
