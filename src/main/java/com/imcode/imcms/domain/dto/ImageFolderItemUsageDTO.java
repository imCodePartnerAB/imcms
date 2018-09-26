package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageFolderItemUsageDTO {
    private String filePath;
    private String folderName;
    private List<ImageFileUsageDTO> usages;

}
