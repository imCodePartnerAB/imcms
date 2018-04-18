package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.TextDocumentTemplate;
import com.imcode.imcms.util.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class TextDocumentTemplateDTO extends TextDocumentTemplate implements Cloneable {

    private static final long serialVersionUID = -3328353784455342916L;

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

    @Override
    public TextDocumentTemplateDTO clone() {
        try {
            final TextDocumentTemplateDTO cloneTextDocumentTemplateDTO = (TextDocumentTemplateDTO) super.clone();

            cloneTextDocumentTemplateDTO.setDocId(null);

            return cloneTextDocumentTemplateDTO;
        } catch (CloneNotSupportedException e) {
            return null; // must not happened
        }

    }
}
