package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.persistence.entity.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentUrlDTO extends DocumentURL {

    private Integer id;

    private String urlFrameName;

    private String urlTarget;

    private String url;

    private String urlText;

    private String urlLanguagePrefix;

    private Integer docId;

    public DocumentUrlDTO(DocumentURL from, Version version) {
        super(from);
        this.docId = version.getDocId();
    }

    public static DocumentUrlDTO createDefault() {
        DocumentUrlDTO documentUrlDTO = new DocumentUrlDTO();

        documentUrlDTO.setUrlFrameName("");
        documentUrlDTO.setUrlTarget("");
        documentUrlDTO.setUrl("");
        documentUrlDTO.setUrlText("");
        documentUrlDTO.setUrlLanguagePrefix("");

        return documentUrlDTO;
    }
}