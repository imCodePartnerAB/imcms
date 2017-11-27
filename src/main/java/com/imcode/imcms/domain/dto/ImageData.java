package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.imcode.imcms.persistence.entity.ImageCropRegionDataHolder;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ImageData implements Serializable {

    private static final long serialVersionUID = -3077752704023867257L;

    protected volatile int width;
    protected volatile int height;
    protected volatile Format format;
    protected volatile String generatedFilename;
    protected volatile CropRegion cropRegion = new CropRegion();
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

    @Data
    @NoArgsConstructor
    public static class CropRegion extends ImageCropRegionDataHolder implements Serializable {
        private static final long serialVersionUID = -586488435877347784L;

        private volatile int cropX1 = -1;
        private volatile int cropY1 = -1;
        private volatile int cropX2 = -1;
        private volatile int cropY2 = -1;

        @JsonIgnore
        private volatile boolean valid;

        public CropRegion(int cropX1, int cropY1, int cropX2, int cropY2) {
            if (cropX1 > cropX2) {
                this.cropX1 = cropX2;
                this.cropX2 = cropX1;
            } else {
                this.cropX1 = cropX1;
                this.cropX2 = cropX2;
            }

            if (cropY1 > cropY2) {
                this.cropY1 = cropY2;
                this.cropY2 = cropY1;
            } else {
                this.cropY1 = cropY1;
                this.cropY2 = cropY2;
            }

            updateValid();
        }

        public CropRegion(ImageCropRegionDataHolder cropRegionDataHolder) {
            super(cropRegionDataHolder);
        }

        @JsonIgnore
        public boolean isValid() {
            updateValid();
            return valid;
        }

        @JsonIgnore
        private void updateValid() {
            valid = (cropX1 >= 0 && cropY1 >= 0 && cropX2 >= 0 && cropY2 >= 0
                    && cropX1 != cropX2 && cropY1 != cropY2);
        }

        @JsonIgnore
        public int getWidth() {
            return valid ? cropX2 - cropX1 : 0;
        }

        @JsonIgnore
        public int getHeight() {
            return valid ? cropY2 - cropY1 : 0;
        }
    }
}
