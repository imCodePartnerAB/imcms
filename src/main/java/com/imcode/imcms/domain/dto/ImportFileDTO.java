package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImportFileDTO {
	private Integer id;
	private String filename;
	private String mime;
	private Integer size;
	@JsonProperty("default")
	private boolean isDefault;
}
