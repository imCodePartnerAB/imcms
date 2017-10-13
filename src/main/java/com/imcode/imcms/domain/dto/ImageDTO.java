package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import imcode.util.image.Format;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDTO extends ImageData {

    private static final long serialVersionUID = -3103155597832120014L;
    private Integer index;
    private Integer docId;
    private String langCode;
    private String name;
    private String path;
    private String url;
    private LoopEntryRefDTO loopEntryRef;
    private String generatedFilePath;

    public ImageDTO(Integer index, Integer docId) {
        this.index = index;
        this.docId = docId;
        this.name = "";
        this.path = "";
        this.url = "";
        this.generatedFilePath = "";
        this.generatedFilename = "";
        this.format = Format.JPEG;
        this.width = 0;
        this.height = 0;
        this.langCode = "en";
    }
}
