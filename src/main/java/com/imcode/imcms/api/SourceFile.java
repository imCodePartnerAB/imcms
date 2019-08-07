package com.imcode.imcms.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceFile implements Cloneable, Serializable {

    private String fileName;
    private String fullPath;
    private FileType fileType;
    private List<String> contents;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum FileType {
        DIRECTORY,
        FILE
    }
}
