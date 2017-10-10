package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private Integer index;
    private String name;
    private String path;
    private String format;
    private Integer width;
    private Integer height;

    public ImageDTO(Integer index) {
        this.index = index;
        this.name = "";
        this.path = "";
        this.format = "";
        this.width = 0;
        this.height = 0;
    }
}
