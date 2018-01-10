package com.imcode.imcms.domain.dto;

import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Document that includes specific things for each document type.
 * Exist because controller can't create generic type instance.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 26.12.17.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UberDocumentDTO extends DocumentDTO {

    private static final long serialVersionUID = 3380096038825841879L;

    private TextDocumentDTO textDocumentDTO;

    private FileDocumentDTO fileDocumentDTO;


    private <T extends DocumentDTO> UberDocumentDTO(T from) {
        super(from);
        this.type = from.type;
    }

    public static <T extends DocumentDTO> UberDocumentDTO of(T from) {
        final UberDocumentDTO uberDocumentDTO = new UberDocumentDTO(from);

        switch (uberDocumentDTO.type) {
            case TEXT:
                uberDocumentDTO.textDocumentDTO = (TextDocumentDTO) from;
                break;

            case FILE:
                uberDocumentDTO.fileDocumentDTO = (FileDocumentDTO) from;
                break;

            default:
                throw new UnsupportedDocumentTypeException(from.type);
        }

        return uberDocumentDTO;
    }

    public <T extends DocumentDTO> T toTypedDocument() {
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
