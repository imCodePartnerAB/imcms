package com.imcode.imcms.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceFile implements Cloneable, Serializable {

    private String fileName;
    private String physicalPath;
    private String fullPath;
    private FileType fileType;
    private byte[] contents;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum FileType {
        DIRECTORY,
        FILE
    }
}
