package com.imcode.imcms.model;

import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public abstract class DocumentFile {

    public DocumentFile(DocumentFile from) {
        setId(from.getId());
        setDocId(from.getDocId());
        setFilename(from.getFilename());
        setCreatedAsImage(from.isCreatedAsImage());
        setMimeType(from.getMimeType());
        setDefaultFile(from.isDefaultFile());
        setFileId(from.getFileId());
        setMultipartFile(from.getMultipartFile());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Integer getDocId();

    public abstract void setDocId(Integer docId);

    public abstract String getFilename();

    public abstract void setFilename(String filename);

    public abstract boolean isCreatedAsImage();

    public abstract void setCreatedAsImage(boolean createdAsImage);

    public abstract String getMimeType();

    public abstract void setMimeType(String mimeType);

    public abstract boolean isDefaultFile();

    public abstract void setDefaultFile(boolean defaultFile);

    public abstract String getFileId();

    public abstract void setFileId(String fileId);

    public abstract MultipartFile getMultipartFile();

    public abstract void setMultipartFile(MultipartFile multipartFile);

}
