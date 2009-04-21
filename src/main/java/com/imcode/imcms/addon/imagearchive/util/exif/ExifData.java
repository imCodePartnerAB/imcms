package com.imcode.imcms.addon.imagearchive.util.exif;

import java.io.Serializable;

public class ExifData implements Serializable {
    private static final long serialVersionUID = 7305457706061909142L;
    
    private String description = "";
    private String artist = "";
    private String copyright = "";
    private int resolution;
    
    
    public ExifData() {
    }

    
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
}
