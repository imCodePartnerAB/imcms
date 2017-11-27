package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.TextDocumentTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDocumentTemplateDTO extends TextDocumentTemplate {
    private Integer docId;

    private String templateName;

    private int templateGroupId;

    private String childrenTemplateName;

    public TextDocumentTemplateDTO(TextDocumentTemplate createFrom) {
        super(createFrom);
    }
}
