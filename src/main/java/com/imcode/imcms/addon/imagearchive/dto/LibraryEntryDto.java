package com.imcode.imcms.addon.imagearchive.dto;

import java.io.Serializable;
import java.util.Date;

public class LibraryEntryDto implements Serializable {
    private static final long serialVersionUID = 1795450778722286059L;
    
    private String fileName;
    private int fileSize;
    private long lastModified;

    
    public LibraryEntryDto() {
    }

    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
    
    public boolean isFileSizeMB() {
        return fileSize >= (1024 * 1024);
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    
    public Date getLastModifiedDate() {
        return new Date(lastModified);
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final LibraryEntryDto other = (LibraryEntryDto) obj;
        if (this.fileName == null || !this.fileName.equals(other.fileName)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        
        return hash;
    }
}
