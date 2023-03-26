package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileDocumentDTO extends DocumentDTO {

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
        setFiles(from.getFiles());
    }

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @SuppressWarnings("unused")
    @ConstructorProperties({"commonContents", "categories", "restrictedPermissions", "documentWasteBasket", "files"})
    public FileDocumentDTO(List<CommonContentDTO> commonContents,
                           Set<CategoryDTO> categories,
                           Set<RestrictedPermissionDTO> restrictedPermissions,
                           DocumentWasteBasketDTO documentWasteBasket,
                           List<DocumentFileDTO> files) {
        super(commonContents, categories, restrictedPermissions, documentWasteBasket);
        this.files = files;
    }

    @Override
    public FileDocumentDTO clone() {
        final FileDocumentDTO cloneFileDocumentDTO = (FileDocumentDTO) super.clone();

        final List<DocumentFileDTO> clonedFiles = getFiles()
                .stream()
                .map(DocumentFileDTO::clone)
                .collect(Collectors.toList());

        cloneFileDocumentDTO.setFiles(clonedFiles);

        return cloneFileDocumentDTO;
    }
}
