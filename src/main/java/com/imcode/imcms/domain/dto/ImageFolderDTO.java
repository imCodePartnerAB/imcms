package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageFolderDTO {

    private String name;
    private String path;
    private List<ImageFileDTO> files;
    private List<ImageFolderDTO> folders;

}
