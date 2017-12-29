package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentFile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentFileDTO extends DocumentFile {

    private Integer id;

    private Integer docId;

    private String filename;

    private boolean createdAsImage;

    private String mimeType;

    private boolean defaultFileId;

    private String fileId;

    public DocumentFileDTO(DocumentFile from) {
        super(from);
    }
}
