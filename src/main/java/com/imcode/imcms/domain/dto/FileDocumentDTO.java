package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileDocumentDTO extends Document implements Serializable {

    private static final long serialVersionUID = -8104577284192730444L;

    private List<DocumentFileDTO> files;

    {
        super.type = Meta.DocumentType.FILE;
    }

    public FileDocumentDTO(Document from) {
        super(from);
    }

    FileDocumentDTO(UberDocumentDTO from) {
        this((Document) from);
        this.files = from.getFiles();
    }

    public static FileDocumentDTO createEmpty(Document from) {
        final FileDocumentDTO fileDocumentDTO = new FileDocumentDTO(from);
        fileDocumentDTO.files = new ArrayList<>();

        return fileDocumentDTO;
    }
}
