package com.imcode.imcms.domain.dto;

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
public class UberDocumentDTO extends IntermediateTextDocumentDTO {
    private static final long serialVersionUID = 3380096038825841879L;
}

class IntermediateTextDocumentDTO extends TextDocumentDTO {
    private static final long serialVersionUID = 2109576866365197230L;
}

// todo: extend each document type
