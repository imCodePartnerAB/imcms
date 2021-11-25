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
    private String size;
    private int numberOfDocuments;  //if this file is a template, the number of documents based on it

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum FileType {
        DIRECTORY,
        FILE
    }
}
