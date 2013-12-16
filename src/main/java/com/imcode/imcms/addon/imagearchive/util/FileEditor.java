package com.imcode.imcms.addon.imagearchive.util;

import imcode.server.Imcms;

import java.beans.PropertyEditorSupport;
import java.io.File;
import org.apache.commons.lang.StringUtils;

public class FileEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        File imcmsPath = Imcms.getPath();

        File file = null;

        if (!StringUtils.isEmpty(text)) {
            file = new File(text);

            if (!file.isAbsolute()) {
                file = new File(imcmsPath, text);
            }
        }
        
        setValue(file);
    }
}
