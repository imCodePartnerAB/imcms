package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.LoopEntryRef;
import imcode.util.image.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;
import java.util.List;

import static java.util.Optional.ofNullable;

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
    private ExifDTO exifInfo;
    private String sizeFormatted;

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
        this.alternateText = " ";
        this.format = Format.JPEG;
        this.width = 0;
        this.height = 0;
        this.border = 0;
        this.align = "";
        this.lowResolutionUrl = "";
        this.target = "";
        this.type = -1;
        this.rotateAngle = 0;
        this.sizeFormatted = "";
        this.compress = false;
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
        this.name = ofNullable(from.name).orElse("");
        this.path = ofNullable(from.path).orElse("");
        this.linkUrl = ofNullable(from.linkUrl).orElse("");
        this.generatedFilePath = ofNullable(from.generatedFilePath).orElse("");
        this.generatedFilename = ofNullable(from.generatedFilename).orElse("");
        this.alternateText = defaultToTrim(from.getAlternateText());
        this.format = from.format;
        this.width = from.width;
        this.height = from.height;
        this.border = from.border;
        this.align = ofNullable(from.align).orElse("");
        this.lowResolutionUrl = ofNullable(from.lowResolutionUrl).orElse("");
        this.target = ofNullable(from.target).orElse("");
        this.type = from.type;
        this.rotateAngle = from.rotateAngle;
        this.sizeFormatted = from.sizeFormatted;
        this.compress = from.compress;
    }

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @ConstructorProperties({"spaceAround"})
    public ImageDTO(SpaceAroundDTO spaceAround) {
        this.spaceAround = spaceAround;
    }

    private String defaultToTrim(String text) {
        return null == text || text.isEmpty() ? " " : text.trim();
    }
}
