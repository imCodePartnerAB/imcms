package imcode.util.image;

import java.util.HashMap;
import java.util.Map;

public enum Format {
    BMP("BMP", 1, "bmp", "image/bmp", true), 
    GIF("GIF", 2, "gif", "image/gif", true), 
    JPEG("JPEG", 3, "jpg", "image/jpeg", true), 
    PNG("PNG", 4, "png", "image/png", true), 
    PSD("PSD", 5, "psd", "image/x-psd", true), 
    SVG("SVG", 6, "svg", "image/svg+xml", false), 
    TIFF("TIFF", 7, "tif", "image/tiff", true), 
    XCF("XCF", 8, "xcf", "image/x-xcf", false), 
    PICT("PICT", 9, "pct", "image/pict", true);

    
    private static final Map<String, Format> FORMAT_MAP = new HashMap<String, Format>(Format.values().length);
    private static final Map<String, Format> EXTENSION_MAP = new HashMap<String, Format>(Format.values().length);
    private static final Map<Integer, Format> ORDINAL_MAP = new HashMap<Integer, Format>(Format.values().length);
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

    
    private Format(String format, int ordinal, String extension, String mimeType, boolean writable) {
        this.format = format;
        this.ordinal = ordinal;
        this.extension = extension;
        this.mimeType = mimeType;
        this.writable = writable;
    }

    
    public String getFormat() {
        return format;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isWritable() {
        return writable;
    }

    public String getMimeType() {
        return mimeType;
    }
    
    
    public static Format findFormat(String format) {
        return FORMAT_MAP.get(format);
    }
    
    public static Format findFormatByExtension(String extension) {
    	return EXTENSION_MAP.get(extension);
    }
    
    public static Format findFormat(int ordinal) {
    	return ORDINAL_MAP.get(ordinal);
    }
}