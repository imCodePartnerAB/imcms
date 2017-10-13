package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import imcode.server.Imcms;
import imcode.server.document.textdocument.ImageSource;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import lombok.*;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public abstract class ImageData {

    protected volatile int width;
    protected volatile int height;
    protected volatile String generatedFilename;
    protected volatile CropRegion cropRegion = new CropRegion();
    protected volatile Format format;

    private volatile Resize resize;
    private volatile RotateDirection rotateDirection = RotateDirection.NORTH;

    @JsonIgnore
    public File getGeneratedFile() {
        File basePath = Imcms.getServices().getConfig().getImagePath();

        return new File(basePath, "generated/" + getGeneratedFilename());
    }

    @JsonIgnore
    public abstract ImageSource getSource();

    public enum RotateDirection {
        NORTH(0, -90, 90),
        EAST(90, 0, 180),
        SOUTH(180, 90, -90),
        WEST(-90, 180, 0);

        private static final Map<Integer, RotateDirection> ANGLE_MAP = new HashMap<>(RotateDirection.values().length);

        static {
            for (RotateDirection direction : RotateDirection.values()) {
                ANGLE_MAP.put(direction.getAngle(), direction);
            }
        }

        @Getter
        private final int angle;
        private final int leftAngle;
        private final int rightAngle;

        RotateDirection(int angle, int leftAngle, int rightAngle) {
            this.angle = angle;
            this.leftAngle = leftAngle;
            this.rightAngle = rightAngle;
        }

        public static RotateDirection getByAngle(int angle) {
            return ANGLE_MAP.get(angle);
        }

        public static RotateDirection getByAngleDefaultIfNull(int angle) {
            RotateDirection direction = getByAngle(angle);

            return (direction != null ? direction : RotateDirection.NORTH);
        }

        public RotateDirection getLeftDirection() {
            return getByAngle(leftAngle);
        }

        public RotateDirection getRightDirection() {
            return getByAngle(rightAngle);
        }

        public boolean isDefault() {
            return this == RotateDirection.NORTH;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CropRegion implements Serializable {
        private static final long serialVersionUID = -586488435877347784L;

        private volatile int cropX1 = -1;
        private volatile int cropY1 = -1;
        private volatile int cropX2 = -1;
        private volatile int cropY2 = -1;

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

        public void updateValid() {
            valid = (cropX1 >= 0 && cropY1 >= 0 && cropX2 >= 0 && cropY2 >= 0
                    && cropX1 != cropX2 && cropY1 != cropY2);
        }

        public int getWidth() {
            return valid ? cropX2 - cropX1 : 0;
        }

        public int getHeight() {
            return valid ? cropY2 - cropY1 : 0;
        }
    }
}
