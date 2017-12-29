package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentFile;
import lombok.Data;

@Data
public class DocumentFileDTO extends DocumentFile {

    private Integer id;

    private String filename;

    private boolean createdAsImage;

    private String mimeType;

    private boolean defaultFileId;

    private String fileId;

}
