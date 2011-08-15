package com.imcode.imcms.addon.imagearchive.command;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class AddImageUploadCommand {
    private CommonsMultipartFile file;
    private int fileCount;

    public AddImageUploadCommand() {
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }
    
    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
}
