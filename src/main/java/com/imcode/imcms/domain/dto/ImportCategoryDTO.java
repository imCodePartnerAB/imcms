package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImportCategoryDTO {
	private String name;
	private String description;
	private String imageUrl;
	private ImportCategoryTypeDTO categoryType;
}
