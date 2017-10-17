package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import lombok.*;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ImageData implements Serializable {

    private static final long serialVersionUID = -3077752704023867257L;
    private static final int GEN_FILE_LENGTH = 255;

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

    public void generateFilename() {
        String suffix = "_" + UUID.randomUUID().toString();

        Format fmt = getFormat();
        if (fmt != null) {
            suffix += "." + fmt.getExtension();
        }

        int maxlength = GEN_FILE_LENGTH - suffix.length();

        String filename = source.getNameWithoutExt();

        if (filename.length() > maxlength) {
            filename = filename.substring(0, maxlength);
        }

        filename = Normalizer.normalize(filename, Normalizer.Form.NFC);

        String[][] specialCharacterReplacements = {
                {"\u00e5", "a"},// å
                {"\u00c5", "A"},
                {"\u00e4", "a"},// ä
                {"\u00c4", "A"},
                {"\u00f6", "o"},// ö
                {"\u00d6", "O"},
                {"\u00e9", "e"},// é
                {"\u00c9", "E"},
                {"\u00f8", "o"},// ø
                {"\u00d8", "O"},
                {"\u00e6", "ae"},// æ
                {"\u00c6", "AE"},
                {"\u0020", "_"} // space
        };
        for (String[] replacement : specialCharacterReplacements) {
            filename = filename.replace(replacement[0], replacement[1]);
        }

        generatedFilename = filename + suffix;
    }

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

        @JsonIgnore
        public void updateValid() {
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
