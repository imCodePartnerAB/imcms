package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TextDocumentDTO extends DocumentDTO implements Serializable {

    private static final long serialVersionUID = -2317764204932918145L;

    private TextDocumentTemplateDTO template;

    {
        type = DocumentType.TEXT;
    }

    public TextDocumentDTO(DocumentDTO from) {
        super(from);
    }

    public static TextDocumentDTO createEmpty(DocumentDTO from) {
        final TextDocumentDTO textDocumentDTO = new TextDocumentDTO(from);
        textDocumentDTO.template = TextDocumentTemplateDTO.createDefault();

        return textDocumentDTO;
    }

}
