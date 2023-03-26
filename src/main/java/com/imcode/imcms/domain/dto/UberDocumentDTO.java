package com.imcode.imcms.domain.dto;

import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.model.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;

/**
 * Document that includes specific things for each document type.
 * Exist because controller can't create generic type instance.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 26.12.17.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UberDocumentDTO extends DocumentDTO {

    private static final long serialVersionUID = 3380096038825841879L;

    private TextDocumentTemplateDTO template;

    private List<DocumentFileDTO> files;

    private DocumentUrlDTO documentURL;

    private UberDocumentDTO(Document from) {
        super(from);
        setType(from.getType());
    }

    private UberDocumentDTO(TextDocumentDTO from) {
        this((Document) from);
        setTemplate(from.getTemplate());
    }

    private UberDocumentDTO(FileDocumentDTO from) {
        this((Document) from);
        setFiles(from.getFiles());
    }

    private UberDocumentDTO(UrlDocumentDTO from) {
        this((Document) from);
        setDocumentURL(from.getDocumentURL());
    }

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @SuppressWarnings("unused")
    @ConstructorProperties({"commonContents", "categories", "restrictedPermissions", "documentWasteBasket", "template", "files", "documentURL"})
    public UberDocumentDTO(List<CommonContentDTO> commonContents,
                           Set<CategoryDTO> categories,
                           Set<RestrictedPermissionDTO> restrictedPermissions,
                           DocumentWasteBasketDTO documentWasteBasket,
                           TextDocumentTemplateDTO template,
                           List<DocumentFileDTO> files,
                           DocumentUrlDTO documentURL) {

        super(commonContents, categories, restrictedPermissions, documentWasteBasket);
        this.documentURL = documentURL;
        this.files = files;
        this.template = template;
    }

    public static <T extends Document> UberDocumentDTO of(T from) {
        if (from instanceof UberDocumentDTO) return (UberDocumentDTO) from;

        switch (from.getType()) {
            case TEXT:
                return new UberDocumentDTO((TextDocumentDTO) from);

            case FILE:
                return new UberDocumentDTO((FileDocumentDTO) from);

            case URL:
                return new UberDocumentDTO((UrlDocumentDTO) from);

            default:
                throw new UnsupportedDocumentTypeException(from.getType());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Document> T toTypedDocument() {
        switch (type) {
            case TEXT:
                return (T) new TextDocumentDTO(this);

            case FILE:
                return (T) new FileDocumentDTO(this);

            case URL:
                return (T) new UrlDocumentDTO(this);

            default:
                throw new UnsupportedDocumentTypeException(type);
        }
    }
}
