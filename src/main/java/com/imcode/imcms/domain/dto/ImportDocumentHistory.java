package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportDocumentHistory {
	private Integer start;
	private Integer end;
	private Page<BasicImportDocumentInfoDTO> basicImportDocuments;
}
