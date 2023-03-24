package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.BasicImportDocumentInfo;
import com.imcode.imcms.model.ImportDocumentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicImportDocumentInfoDTO extends BasicImportDocumentInfo {
	private Integer id;
	private Integer metaId;
	private ImportDocumentStatus status;

	public BasicImportDocumentInfoDTO(BasicImportDocumentInfo from) {
		super(from);
	}

	public BasicImportDocumentInfoDTO(Integer id, ImportDocumentStatus status) {
		this.id = id;
		this.status = status;
	}

	public BasicImportDocumentInfoDTO(Integer id, Integer metaId, ImportDocumentStatus status) {
		this.id = id;
		this.metaId = metaId;
		this.status = status;
	}
}
