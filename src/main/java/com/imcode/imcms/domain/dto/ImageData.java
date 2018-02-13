package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ImageData implements Documentable, Serializable {

    private static final long serialVersionUID = -3077752704023867257L;

    protected volatile int width;
    protected volatile int height;
    protected volatile Format format;
    protected volatile String generatedFilename;
    protected volatile ImageCropRegionDTO cropRegion = new ImageCropRegionDTO();
    protected volatile ImageSource source = new NullImageSource();

    private volatile Resize resize;
    private volatile RotateDirection rotateDirection = RotateDirection.NORTH;

    @JsonIgnore
    public boolean isEmpty() {
        return source.isEmpty();
    }

    @JsonIgnore
    public ImageSource getSource() {
        if (isEmpty()) {
            return new NullImageSource();
        }
        return source;
    }

    @JsonDeserialize(as = ImagesPathRelativePathImageSource.class)
    public void setSource(ImageSource source) {
        this.source = Objects.requireNonNull(source, "image source can not be null");
    }

    @Getter
    @AllArgsConstructor
    public enum RotateDirection {
        NORTH(0),
        EAST(90),
        SOUTH(180),
        WEST(-90);

        private final int angle;

        @JsonIgnore
        public boolean isDefault() {
            return this == RotateDirection.NORTH;
        }
    }

}
