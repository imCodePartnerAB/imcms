package com.imcode.imcms.domain.dto;

import imcode.server.document.index.ImageFileIndex;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class SearchImageFileQueryDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 8802994228436485574L;

	private String term;

	private FilterBy filterBy = FilterBy.ALL;

	private ImageFilePageRequestDTO page;

	public SearchImageFileQueryDTO(String term) {
		this.term = term;
	}

	@Getter
    public enum FilterBy {
		ALL(null),
		PHOTOGRAPHER(ImageFileIndex.FIELD__PHOTOGRAPHER),
		UPLOADED_BY(ImageFileIndex.FIELD__UPLOADED_BY),
		COPYRIGHT(ImageFileIndex.FIELD__COPYRIGHT),
		ALT_TEXT(ImageFileIndex.ALT_TEXT),
		DESCRIPTION_TEXT(ImageFileIndex.DESCRIPTION_TEXT),
		BEFORE_LICENSE_PERIOD_START(ImageFileIndex.LICENSE_PERIOD_START),
		AFTER_LICENSE_PERIOD_START(ImageFileIndex.LICENSE_PERIOD_START),
		BEFORE_LICENSE_PERIOD_END(ImageFileIndex.LICENSE_PERIOD_END),
		AFTER_LICENSE_PERIOD_END(ImageFileIndex.LICENSE_PERIOD_END);

		private final String indexFieldName;

		FilterBy(String indexFieldName) {
			this.indexFieldName = indexFieldName;
		}

    }
}
