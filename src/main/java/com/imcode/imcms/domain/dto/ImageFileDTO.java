package com.imcode.imcms.domain.dto;

import imcode.util.image.Format;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageFileDTO {

    private String name;
    private String path;
    private Format format;
    private String uploaded;
    private String resolution;
    private String size;
    private Integer width;
    private Integer height;
    private ExifDTO exifInfo;
}
