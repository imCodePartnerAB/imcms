package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ExternalFilesCommand implements Serializable {
    private static final long serialVersionUID = 7135495070544485257L;
    
    private CommonsMultipartFile file;
    private String[] fileNames;
    
    private String upload;
    private String activate;
    private String erase;
    
    
    public ExternalFilesCommand() {
    }

    
    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String[] getFileNames() {
        return fileNames;
    }

    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }

    public String getErase() {
        return erase;
    }

    public void setErase(String erase) {
        this.erase = erase;
    }
}
