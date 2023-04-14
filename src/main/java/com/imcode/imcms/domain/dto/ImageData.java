package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.imcode.imcms.model.SpaceAround;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.server.document.textdocument.FileStorageImageSource;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ImageData implements Documentable, Serializable {

    private static final long serialVersionUID = -3077752704023867257L;

    protected int width;
    protected int height;
    protected Format format;
    protected String generatedFilename;
    protected ImageCropRegionDTO cropRegion = new ImageCropRegionDTO();
    @EqualsAndHashCode.Exclude
    protected ImageSource source = new NullImageSource();
    protected SpaceAroundDTO spaceAround = new SpaceAroundDTO();
    protected boolean compress;

    private Resize resize;
    private RotateDirection rotateDirection = RotateDirection.NORTH;


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

    @JsonDeserialize(as = FileStorageImageSource.class)
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

        @JsonIgnore
        public static RotateDirection fromAngle(int angle) {
            if (angle > 180) {
                angle = angle - (angle / 180) * 180;
            }

            if (angle < -90) {
                angle = angle - (angle / -90) * -90;
            }

            switch (angle) {
                case -90:
                    return WEST;
                case 0:
                    return NORTH;
                case 90:
                    return EAST;
                case 180:
                    return SOUTH;
                default: {
                    if (angle < -45) return WEST;
                    if (angle > 135) return SOUTH;
                    if (angle > 45) return EAST;
                    return NORTH;
                }
            }
        }

        @JsonIgnore
        public int toAngle() {
            return angle;
        }
    }

    public SpaceAround getSpaceAround() {
        return spaceAround;
    }

    public void setSpaceAround(SpaceAround spaceAround) {
        this.spaceAround = new SpaceAroundDTO(spaceAround);
    }
}
