package com.imcode.imcms.domain.dto;

import imcode.util.image.Format;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDTO {

    private Integer index;
    private Integer docId;
    private String langCode;
    private String name;
    private String path;
    private String url;
    private String format;
    private Integer width;
    private Integer height;
    private LoopEntryRefDTO loopEntryRef;
    private String generatedFilePath;
    private String generatedFileName;

    public ImageDTO(Integer index, Integer docId) {
        this.index = index;
        this.docId = docId;
        this.name = "";
        this.path = "";
        this.url = "";
        this.generatedFilePath = "";
        this.generatedFileName = "";
        this.format = Format.JPEG.name();
        this.width = 0;
        this.height = 0;
        this.langCode = "en";
    }
}
