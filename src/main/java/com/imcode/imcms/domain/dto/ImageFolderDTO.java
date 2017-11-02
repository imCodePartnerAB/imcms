package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageFolderDTO {

    private String name;
    private String path;
    private List<ImageFileDTO> files;
    private List<ImageFolderDTO> folders;

    public ImageFolderDTO(String name, String path) {
        this.name = name;
        this.path = path;
        this.files = Collections.emptyList();
        this.folders = Collections.emptyList();
    }

    public ImageFolderDTO(String name) {
        this(name, "/" + name);
    }
}
