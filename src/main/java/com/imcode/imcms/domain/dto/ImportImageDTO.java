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
	private Integer width;
	private Integer height;
	private Integer border;
	private Integer verticalSpace;
	private Integer horizontalSpace;
	private String target;
	private String align;
	private String altText;
	private String lowResolutionUrl;
	private String imageUrl;
	private String linkUrl;
	private Integer type;
	private String format;
	private Integer rotateAngle;
	private Integer cropX1;
	private Integer cropY1;
	private Integer cropX2;
	private Integer cropY2;
	private Long archiveImageId;
	private Integer resize;
}
