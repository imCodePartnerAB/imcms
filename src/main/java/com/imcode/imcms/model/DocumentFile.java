package com.imcode.imcms.model;

public abstract class DocumentFile {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getFilename();

    public abstract void setFilename(String filename);

    public abstract boolean isCreatedAsImage();

    public abstract void setCreatedAsImage(boolean createdAsImage);

    public abstract String getMimeType();

    public abstract void setMimeType(String mimeType);

    public abstract boolean isDefaultFileId();

    public abstract void setDefaultFileId(boolean defaultFileId);

    public abstract String getFileId();

    public abstract void setFileId(String fileId);

}
