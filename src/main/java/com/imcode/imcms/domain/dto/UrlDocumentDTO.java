package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
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
public class UrlDocumentDTO extends DocumentDTO implements Serializable {

    private static final long serialVersionUID = -8053993136553115412L;

    private DocumentUrlDTO documentURL;

    {
        super.type = Meta.DocumentType.URL;
    }

    public UrlDocumentDTO(Document from) {
        super(from);
    }

    UrlDocumentDTO(UberDocumentDTO from) {
        this((Document) from);
        this.documentURL = from.getDocumentURL();
    }

    @Override
    public UrlDocumentDTO clone() {
        final UrlDocumentDTO cloneUrlDocumentDTO = (UrlDocumentDTO) super.clone();

        final DocumentUrlDTO clonedDocumentUrlDTO = cloneUrlDocumentDTO.getDocumentURL();
        if (clonedDocumentUrlDTO != null) {
            cloneUrlDocumentDTO.setDocumentURL(clonedDocumentUrlDTO.clone());
        }

        return cloneUrlDocumentDTO;

    }

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @SuppressWarnings("unused")
    @ConstructorProperties({"commonContents", "categories", "restrictedPermissions", "documentWasteBasket", "documentURL"})
    public UrlDocumentDTO(List<CommonContentDTO> commonContents,
                          Set<CategoryDTO> categories,
                          Set<RestrictedPermissionDTO> restrictedPermissions,
                          DocumentWasteBasketDTO documentWasteBasket,
                          DocumentUrlDTO documentURL) {
        super(commonContents, categories, restrictedPermissions, documentWasteBasket);
        this.documentURL = documentURL;
    }
}
