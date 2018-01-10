package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface DocumentSaver<Document extends DocumentDTO> {

    int save(Document saveMe);

}
