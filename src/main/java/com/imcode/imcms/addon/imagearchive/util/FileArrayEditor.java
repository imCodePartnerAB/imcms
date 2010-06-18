package com.imcode.imcms.addon.imagearchive.util;

import imcode.server.Imcms;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class FileArrayEditor extends PropertyEditorSupport {
    private static final File[] EMPTY = new File[0];
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        File imcmsPath = Imcms.getPath();
        File[] paths = EMPTY;
        
        String[] parts = StringUtils.split(text, ';');
        
        if (!ArrayUtils.isEmpty(parts)) {
            List<File> files = new ArrayList<File>(parts.length);
            
            for (String part : parts) {
                part = StringUtils.trimToNull(part);
                
                if (part != null) {
                    File file = new File(part);
                    
                    if (!file.isAbsolute()) {
                        file = new File(imcmsPath, part);
                    }
                    
                    files.add(file);
                }
            }
            
            paths = files.toArray(new File[files.size()]);
        }
        
        setValue(paths);
    }
}
