package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class DocumentFile {

    public DocumentFile(DocumentFile from) {
        setId(from.getId());
        setFilename(from.getFilename());
        setCreatedAsImage(from.isCreatedAsImage());
        setMimeType(from.getMimeType());
        setDefaultFileId(from.isDefaultFileId());
        setFileId(from.getFileId());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Integer getDocId();

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
