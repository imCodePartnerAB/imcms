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
    protected boolean inText;
    private boolean allLanguages;
    private String alternateText;
    private String linkUrl;

    /**
     * Two-letter language code ISO-639-1
     */
    private String langCode;
    private String name;
    private String path;
    private LoopEntryRefDTO loopEntryRef;
    private String generatedFilePath;

    private int border;
    private String align;
    private String lowResolutionUrl;
    private int verticalSpace;
    private int horizontalSpace;
    private String target;
    private int type;
    private int rotateAngle;
    private Long archiveImageId;

    public ImageDTO(Integer index, Integer docId, LoopEntryRef loopEntryRef, String langCode) {
        this.index = index;
        this.docId = docId;
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefDTO(loopEntryRef);
        this.langCode = langCode;
        this.name = "";
        this.path = "";
        this.linkUrl = "";
        this.generatedFilePath = "";
        this.generatedFilename = "";
        this.alternateText = "";
        this.format = Format.JPEG;
        this.width = 0;
        this.height = 0;
        this.border = 0;
        this.align = "";
        this.lowResolutionUrl = "";
        this.verticalSpace = 0;
        this.horizontalSpace = 0;
        this.target = "";
        this.type = -1;
        this.rotateAngle = 0;
    }

    public ImageDTO(Integer index, Integer docId) {
        this(index, docId, null, "en");
    }

    public ImageDTO(ImageDTO from) {
        this.index = from.index;
        this.docId = from.docId;
        this.loopEntryRef = (from.loopEntryRef == null) ? null : new LoopEntryRefDTO(from.loopEntryRef);
        this.langCode = from.langCode;
        this.inText = from.inText;
        this.name = from.name;
        this.path = from.path;
        this.linkUrl = from.linkUrl;
        this.generatedFilePath = from.generatedFilePath;
        this.generatedFilename = from.generatedFilename;
        this.alternateText = from.alternateText;
        this.format = from.format;
        this.width = from.width;
        this.height = from.height;
    }
}
