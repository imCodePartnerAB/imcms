package com.imcode.imcms.addon.imagearchive.util.exif;

import org.apache.sanselan.common.RationalNumber;

import java.io.Serializable;
import java.util.Date;

public class ExifData implements Serializable {
    private static final long serialVersionUID = 7305457706061909142L;

    private String manufacturer;
    private String model;
    private String compression;
    private RationalNumber exposure;
    private String exposureProgram = "";
    private RationalNumber fStop;
    private Date dateOriginal;
    private Date dateDigitized;
    private String description = "";
    private String artist = "";
    private String copyright = "";
    private Flash flash;
    private RationalNumber focalLength;
    private String colorSpace;
    private Integer xResolution;
    private Integer yResolution;
    /* 2 = dpi, 3 = dpcm*/
    private Integer resolutionUnit;
    private Integer pixelXDimension;
    private Integer pixelYDimension;
    private Integer ISO;


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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public Double getExposure() {
        return exposure != null ? exposure.doubleValue() : null;
    }

    public void setExposure(RationalNumber exposure) {
        this.exposure = exposure;
    }

    public String getExposureProgram() {
        return exposureProgram;
    }

    public void setExposureProgram(String exposureProgram) {
        this.exposureProgram = exposureProgram;
    }

    public Float getfStop() {
        return fStop != null ? fStop.floatValue() : null;
    }

    public void setfStop(RationalNumber fStop) {
        this.fStop = fStop;
    }

    public Date getDateOriginal() {
        return dateOriginal;
    }

    public void setDateOriginal(Date dateOriginal) {
        this.dateOriginal = dateOriginal;
    }

    public Date getDateDigitized() {
        return dateDigitized;
    }

    public void setDateDigitized(Date dateDigitized) {
        this.dateDigitized = dateDigitized;
    }

    public Flash getFlash() {
        return flash;
    }

    public void setFlash(Flash flash) {
        this.flash = flash;
    }

    public Float getFocalLength() {
        return focalLength != null ? focalLength.floatValue() : null;
    }

    public void setFocalLength(RationalNumber focalLength) {
        this.focalLength = focalLength;
    }

    public String getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
    }

    public Integer getxResolution() {
        return xResolution;
    }

    public void setxResolution(Integer xResolution) {
        this.xResolution = xResolution;
    }

    public Integer getyResolution() {
        return yResolution;
    }

    public void setyResolution(Integer yResolution) {
        this.yResolution = yResolution;
    }

    public Integer getResolutionUnit() {
        return resolutionUnit;
    }

    public void setResolutionUnit(Integer resolutionUnit) {
        this.resolutionUnit = resolutionUnit;
    }

    public Integer getPixelXDimension() {
        return pixelXDimension;
    }

    public void setPixelXDimension(Integer pixelXDimension) {
        this.pixelXDimension = pixelXDimension;
    }

    public Integer getPixelYDimension() {
        return pixelYDimension;
    }

    public void setPixelYDimension(Integer pixelYDimension) {
        this.pixelYDimension = pixelYDimension;
    }

    public Integer getISO() {
        return ISO;
    }

    public void setISO(Integer ISO) {
        this.ISO = ISO;
    }
}
