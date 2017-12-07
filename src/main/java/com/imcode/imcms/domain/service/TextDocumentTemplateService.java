package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;

import java.util.Optional;

/**
 * Service for work with templates in relation to text documents.
 */
public interface TextDocumentTemplateService {

    TextDocumentTemplateDTO save(TextDocumentTemplateDTO saveMe);

    Optional<TextDocumentTemplateDTO> get(int docId);

}
