package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.Meta;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface EmptyTypedDocumentCreatingService<Document extends DocumentDTO> {

    Document createEmpty(Meta.DocumentType type);

}
