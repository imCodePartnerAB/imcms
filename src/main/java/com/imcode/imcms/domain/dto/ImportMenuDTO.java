package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImportMenuDTO {
	private Integer index;
	@JsonProperty("sort_order")
	private String typeSort;
	private List<ImportMenuItemDTO> menuItems=new ArrayList<>();
}
