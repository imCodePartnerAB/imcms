package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentFile;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class DocumentFileDTO extends DocumentFile {

    private Integer id;

    private Integer docId;

    private String filename;

    private boolean createdAsImage;

    private String mimeType;

    private boolean defaultFile;

    private String fileId;

    private MultipartFile multipartFile;

    public DocumentFileDTO(DocumentFile from) {
        super(from);
    }
}
