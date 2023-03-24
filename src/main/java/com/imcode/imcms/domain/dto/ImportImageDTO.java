package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImportImageDTO {
	private Integer index;
	private String name;
	private String generatedFilename;
	private Integer border;
	private String align;
	private String target;
	private String path;
	private Integer width;
	private Integer height;
	private Integer horizontalSpace;
	private Integer angle;
	private String altText;
	private Long archiveImageId;
	private String linkUrl;
	private String lowResolutionUrl;
	private String format;
}
