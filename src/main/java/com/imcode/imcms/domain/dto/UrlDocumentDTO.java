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
import java.util.Optional;
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

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @SuppressWarnings("unused")
    @ConstructorProperties({"commonContents", "categories", "restrictedPermissions", "documentURL"})
    public UrlDocumentDTO(List<CommonContentDTO> commonContents,
                          Set<CategoryDTO> categories,
                          Set<RestrictedPermissionDTO> restrictedPermissions,
                          DocumentUrlDTO documentURL) {
        super(commonContents, categories, restrictedPermissions);
        this.documentURL = documentURL;
    }

    @Override
    public UrlDocumentDTO clone() throws CloneNotSupportedException {
        final UrlDocumentDTO cloneUrlDocumentDTO = (UrlDocumentDTO) super.clone();

        Optional.ofNullable(cloneUrlDocumentDTO.getDocumentURL())
                .ifPresent(template -> {
                    try {
                        cloneUrlDocumentDTO.setDocumentURL(cloneUrlDocumentDTO.getDocumentURL().clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                });

        return cloneUrlDocumentDTO;
    }
}
