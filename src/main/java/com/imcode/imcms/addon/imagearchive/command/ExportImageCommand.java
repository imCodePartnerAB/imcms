package com.imcode.imcms.addon.imagearchive.command;

import imcode.util.image.Format;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExportImageCommand implements Serializable {
    private static final long serialVersionUID = 2298263043099132841L;
    
    private String export;
    private Integer width;
    private Integer height;
    private Integer quality = 100;
    private List<Format> fileFormats = new ArrayList<Format>();
    private Short fileFormat;
    private int[] qualities = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    
    
    public ExportImageCommand() {
        for (Format format : Format.values()) {
            if (format.isWritable()) {
                fileFormats.add(format);
            }
        }
    }

    
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Short getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(Short fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public List<Format> getFileFormats() {
        return fileFormats;
    }

    public void setFileFormats(List<Format> fileFormats) {
        this.fileFormats = fileFormats;
    }

    public int[] getQualities() {
        return qualities;
    }

    public void setQualities(int[] qualities) {
        this.qualities = qualities;
    }
}
