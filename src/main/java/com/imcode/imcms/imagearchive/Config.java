package com.imcode.imcms.imagearchive;

import imcode.util.io.FileUtility;
import org.apache.commons.collections.BidiMap;

import java.io.File;

public class Config {
    private File storagePath;
    private File tmpPath;
    private File imageMagickPath;
    private File imagesPath;
    private File librariesPath;
    private String usersLibraryFolder;
    private File[] oldLibraryPaths;
    private BidiMap languages;

    private long maxImageUploadSize;
    private long maxZipUploadSize;


    public Config() {
    }


    public File getStoragePath() {
        return FileUtility.getFileFromWebappRelativePath(storagePath.toString());
    }

    public void setStoragePath(File storagePath) {
        this.storagePath = storagePath;
    }

    public File getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(File tmpPath) {
        this.tmpPath = tmpPath;
    }

    public File getImagesPath() {
        return FileUtility.getFileFromWebappRelativePath(imagesPath.toString());
    }

    public void setImagesPath(File imagesPath) {
        this.imagesPath = imagesPath;
    }

    public File getLibrariesPath() {
        return FileUtility.getFileFromWebappRelativePath(librariesPath.toString());
    }

    public void setLibrariesPath(File librariesPath) {
        this.librariesPath = librariesPath;
    }

    public String getUsersLibraryFolder() {
        return usersLibraryFolder;
    }

    public void setUsersLibraryFolder(String usersLibraryFolder) {
        this.usersLibraryFolder = usersLibraryFolder;
    }

    public File[] getOldLibraryPaths() {
        return oldLibraryPaths;
    }

    public void setOldLibraryPaths(File[] oldLibraryPaths) {
        this.oldLibraryPaths = oldLibraryPaths;
    }

    public long getMaxImageUploadSize() {
        return maxImageUploadSize;
    }

    public void setMaxImageUploadSize(long maxImageUploadSize) {
        this.maxImageUploadSize = maxImageUploadSize;
    }

    public long getMaxZipUploadSize() {
        return maxZipUploadSize;
    }

    public void setMaxZipUploadSize(long maxZipUploadSize) {
        this.maxZipUploadSize = maxZipUploadSize;
    }

    public File getImageMagickPath() {
        return imageMagickPath;
    }

    public void setImageMagickPath(File imageMagickPath) {
        this.imageMagickPath = imageMagickPath;
    }

    public BidiMap getLanguages() {
        return languages;
    }

    public void setLanguages(BidiMap languages) {
        this.languages = languages;
    }
}
