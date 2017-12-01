package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.TextDocumentTemplate;
import com.imcode.imcms.util.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDocumentTemplateDTO extends TextDocumentTemplate {
    private Integer docId;

    private String templateName;

    private String childrenTemplateName;

    public TextDocumentTemplateDTO(TextDocumentTemplate createFrom) {
        super(createFrom);
    }

    public static TextDocumentTemplateDTO createDefault() {
        return Value.with(new TextDocumentTemplateDTO(), textDocumentTemplateDTO -> {
            textDocumentTemplateDTO.templateName = DEFAULT_TEMPLATE_NAME;
            textDocumentTemplateDTO.childrenTemplateName = DEFAULT_TEMPLATE_NAME;
        });
    }
}
