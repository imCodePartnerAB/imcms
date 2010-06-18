package com.imcode.imcms.addon.imagearchive.util.image;

import java.util.HashMap;
import java.util.Map;
import com.imcode.imcms.addon.imagearchive.entity.Images;

public enum Format {
    BMP("BMP", Images.FORMAT_BMP, "bmp", "image/bmp", true), 
    GIF("GIF", Images.FORMAT_GIF, "gif", "image/gif", true), 
    JPEG("JPEG", Images.FORMAT_JPEG, "jpg", "image/jpeg", true), 
    PDF("PDF", Images.FORMAT_PDF, "pdf", "application/pdf", true), 
    PNG("PNG", Images.FORMAT_PNG, "png", "image/png", true), 
    PICT("PICT", Images.FORMAT_PICT, "pct", "image/pict", true), 
    PS("PS", Images.FORMAT_PS, "ps", "application/postscript", true), 
    PSD("PSD", Images.FORMAT_PSD, "psd", "image/x-psd", true), 
    SVG("SVG", Images.FORMAT_SVG, "svg", "image/svg+xml", false), 
    TIFF("TIFF", Images.FORMAT_TIFF, "tif", "image/tiff", true), 
    XCF("XCF", Images.FORMAT_XCF, "xcf", "image/x-xcf", false);

    
    private static final Map<String, Format> formatMap = new HashMap<String, Format>(Format.values().length);
    static {
        for (Format format : Format.values()) {
            formatMap.put(format.getFormat(), format);
        }
    }
    
    private final String format;
    private final short imageFormat;
    private final String extension;
    private final String mimeType;
    private final boolean writable;

    
    private Format(String format, short imageFormat, String extension, String mimeType, boolean writable) {
        this.format = format;
        this.imageFormat = imageFormat;
        this.extension = extension;
        this.mimeType = mimeType;
        this.writable = writable;
    }

    
    public String getFormat() {
        return format;
    }

    public short getImageFormat() {
        return imageFormat;
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
        return formatMap.get(format);
    }
    
    public static Format findFormatByImageFormat(short imageFormat) {
        for (Format format : Format.values()) {
            if (format.getImageFormat() == imageFormat) {
                return format;
            }
        }
        
        return null;
    }
}
