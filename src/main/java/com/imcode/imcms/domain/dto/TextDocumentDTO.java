package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextDocumentDTO extends DocumentDTO implements Serializable {

    private static final long serialVersionUID = -2317764204932918145L;

    private TextDocumentTemplateDTO template;

    {
        super.type = DocumentType.TEXT;
    }

    public TextDocumentDTO(Document from) {
        super(from);
    }

    TextDocumentDTO(UberDocumentDTO from) {
        super(from);
        this.template = from.getTemplate();
    }

    @Override
    public TextDocumentDTO clone() {
        final TextDocumentDTO cloneTextDocumentDTO = (TextDocumentDTO) super.clone();

        final TextDocumentTemplateDTO textDocumentTemplateDTO = cloneTextDocumentDTO.getTemplate();
        if (textDocumentTemplateDTO != null) {
            cloneTextDocumentDTO.setTemplate(textDocumentTemplateDTO.clone());
        }

        return cloneTextDocumentDTO;

    }

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @SuppressWarnings("unused")
    @ConstructorProperties({"commonContents", "categories", "restrictedPermissions", "documentWasteBasket", "template"})
    public TextDocumentDTO(List<CommonContentDTO> commonContents,
                           Set<CategoryDTO> categories,
                           Set<RestrictedPermissionDTO> restrictedPermissions,
                           DocumentWasteBasketDTO documentWasteBasket,
                           TextDocumentTemplateDTO template) {
        super(commonContents, categories, restrictedPermissions, documentWasteBasket);
        this.template = template;
    }

}
