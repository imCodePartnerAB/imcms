package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.imcms.model.DocumentFile;
import imcode.util.io.InputStreamSource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class DocumentFileDTO extends DocumentFile implements Cloneable {

    private static final long serialVersionUID = 7090558552422447802L;

    private Integer id;

    private Integer docId;

    private String filename;

    private String originalFilename;

    private boolean createdAsImage;

    private String mimeType;

    private boolean defaultFile;

    private String fileId;

    private MultipartFile multipartFile;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private InputStreamSource inputStreamSource;

    public DocumentFileDTO(DocumentFile from) {
        super(from);
    }

    public String getOriginalFilename() {
        return StringUtils.defaultIfBlank(originalFilename, filename);
    }

    @Override
    public DocumentFileDTO clone() {
        try {
            final DocumentFileDTO cloneDocumentFileDTO = (DocumentFileDTO) super.clone();
            cloneDocumentFileDTO.setId(null);
            cloneDocumentFileDTO.setDocId(null);

            return cloneDocumentFileDTO;
        } catch (CloneNotSupportedException e) {
            return null; // must not happened
        }
    }
}
