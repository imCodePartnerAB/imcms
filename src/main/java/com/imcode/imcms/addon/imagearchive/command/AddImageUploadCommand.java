package com.imcode.imcms.addon.imagearchive.command;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class AddImageUploadCommand {
    private CommonsMultipartFile file;

    public AddImageUploadCommand() {
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }
}
