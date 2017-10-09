package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDTO {
    private Integer index;
    private String name;
    private String path;
    private String format;
    private Integer width;
    private Integer height;
}
