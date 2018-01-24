package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.persistence.entity.Version;
import lombok.Data;

@Data
public class DocumentUrlDTO extends DocumentURL {

    private Integer id;

    private String urlFrameName;

    private String urlTarget;

    private String url;

    private String urlText;

    private String urlLanguagePrefix;

    private Version version;

    public DocumentUrlDTO(DocumentURL from) {
        super(from);
    }
}