package imcode.util.image;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Format {
    BMP("BMP", 1, "bmp", "image/bmp", true),
    GIF("GIF", 2, "gif", "image/gif", true),
    JPG("JPG", 3, "jpg", "image/jpeg", true),
    JPEG("JPEG", 4, "jpeg", "image/jpeg", true),
    PNG("PNG", 5, "png", "image/png", true),
    PSD("PSD", 6, "psd", "image/x-psd", true),
    SVG("SVG", 7, "svg", "image/svg+xml", false),
    TIFF("TIFF", 8, "tif", "image/tiff", true),
    XCF("XCF", 9, "xcf", "image/x-xcf", false),
    PICT("PICT", 10, "pct", "image/pict", true);


    private static final Map<String, Format> FORMAT_MAP = new HashMap<>(Format.values().length);
    private static final Map<String, Format> EXTENSION_MAP = new HashMap<>(Format.values().length);
    private static final Map<Integer, Format> ORDINAL_MAP = new HashMap<>(Format.values().length);

    static {
        for (Format format : Format.values()) {
            FORMAT_MAP.put(format.getFormat(), format);
            EXTENSION_MAP.put(format.getExtension(), format);
            ORDINAL_MAP.put(format.getOrdinal(), format);
        }
    }

    private final String format;
    private final int ordinal;
    private final String extension;
    private final String mimeType;
    private final boolean writable;


    Format(String format, int ordinal, String extension, String mimeType, boolean writable) {
        this.format = format;
        this.ordinal = ordinal;
        this.extension = extension;
        this.mimeType = mimeType;
        this.writable = writable;
    }

    public static Format findFormat(String formatOrExtension) {
        final Format format = FORMAT_MAP.get(formatOrExtension);

        if (format != null) {
            return format;
        }

        return EXTENSION_MAP.get(formatOrExtension);
    }

    public static boolean isImage(String formatOrExtension) {
        return FORMAT_MAP.containsKey(formatOrExtension.toUpperCase()) || EXTENSION_MAP.containsKey(formatOrExtension);
    }

}
