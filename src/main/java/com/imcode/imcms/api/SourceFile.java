package com.imcode.imcms.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SourceFile implements Cloneable, Serializable {

    private String fileName;
    private String fullPath;
    private FileType fileType;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum FileType {
        FILE,
        DIRECTORY
    }
}
