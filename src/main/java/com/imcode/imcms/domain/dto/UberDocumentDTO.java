package com.imcode.imcms.domain.dto;

import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.model.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class UberDocumentDTO extends Document {

    private static final long serialVersionUID = 3380096038825841879L;

    private TextDocumentDTO textDocumentDTO;

    private FileDocumentDTO fileDocumentDTO;


    private UberDocumentDTO(Document from) {
        super(from);
        this.type = from.getType();
    }

    public static <T extends Document> UberDocumentDTO of(T from) {
        final UberDocumentDTO uberDocumentDTO = new UberDocumentDTO(from);

        switch (uberDocumentDTO.type) {
            case TEXT:
                uberDocumentDTO.textDocumentDTO = (TextDocumentDTO) from;
                break;

            case FILE:
                uberDocumentDTO.fileDocumentDTO = (FileDocumentDTO) from;
                break;

            default:
                throw new UnsupportedDocumentTypeException(from.getType());
        }

        return uberDocumentDTO;
    }

    public <T extends Document> T toTypedDocument() {
        switch (type) {
            case TEXT:
                return (T) textDocumentDTO;

            case FILE:
                return (T) fileDocumentDTO;

            default:
                throw new UnsupportedDocumentTypeException(type);
        }
    }

}
