package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.LoopEntryRef;
import imcode.util.image.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDTO extends ImageData {

    private static final long serialVersionUID = -3103155597832120014L;
    private Integer index;
    private Integer docId;

    /**
     * Two-letter language code ISO-639-1
     */
    private String langCode;
    private String name;
    private String path;
    private String url;
    private LoopEntryRefDTO loopEntryRef;
    private String generatedFilePath;

    public ImageDTO(Integer index, Integer docId, LoopEntryRef loopEntryRef, String langCode) {
        this.index = index;
        this.docId = docId;
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefDTO(loopEntryRef);
        this.langCode = langCode;
        this.name = "";
        this.path = "";
        this.url = "";
        this.generatedFilePath = "";
        this.generatedFilename = "";
        this.format = Format.JPEG;
        this.width = 0;
        this.height = 0;
    }

    public ImageDTO(Integer index, Integer docId) {
        this(index, docId, null, "en");
    }
}
