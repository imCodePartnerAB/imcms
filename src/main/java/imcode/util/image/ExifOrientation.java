package imcode.util.image;

public enum ExifOrientation {
    TOP(1),
    FLIPPED_TOP(2),
    BOTTOM(3),
    FLIPPED_BOTTOM(4),
    FLIPPED_LEFT(5),
    LEFT(6),
    FLIPPED_RIGHT(7),
    RIGHT(8);

    private final int value;

    ExifOrientation(int value) {
        this.value = value;
    }

    public static ExifOrientation fromValue(int value) {
        for(ExifOrientation orientation: ExifOrientation.values()) {
            if(orientation.value == value)
                return orientation;
        }

        return null;
    }
}
