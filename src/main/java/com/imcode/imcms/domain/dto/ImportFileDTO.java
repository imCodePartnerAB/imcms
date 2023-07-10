package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImportFileDTO {
	private String id;
	private String variantName;
	private String filename;
	private String mime;
	private Boolean createdAsImage;
	private Integer size;
	@JsonProperty("default")
	private boolean isDefault;
}
