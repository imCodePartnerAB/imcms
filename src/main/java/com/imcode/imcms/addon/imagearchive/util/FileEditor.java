package com.imcode.imcms.addon.imagearchive.util;

import imcode.server.Imcms;

import java.beans.PropertyEditorSupport;
import java.io.File;

public class FileEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        File imcmsPath = Imcms.getPath();
        
        File file = new File(text);
        
        if (!file.isAbsolute()) {
            file = new File(imcmsPath, text);
        }
        
        setValue(file);
    }
}
