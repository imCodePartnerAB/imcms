package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentFile;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class DocumentFileDTO extends DocumentFile implements Cloneable {

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

    @Override
    protected DocumentFileDTO clone() {
        try {
            final DocumentFileDTO cloneDocumentFileDTO = (DocumentFileDTO) super.clone();
            cloneDocumentFileDTO.setId(null);
            cloneDocumentFileDTO.setDocId(null);

            return cloneDocumentFileDTO;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
