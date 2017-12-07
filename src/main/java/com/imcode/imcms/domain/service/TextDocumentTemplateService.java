package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.TextDocumentTemplate;

import java.util.Optional;

/**
 * Service for work with templates in relation to text documents.
 */
public interface TextDocumentTemplateService {

    TextDocumentTemplate save(TextDocumentTemplate saveMe);

    Optional<TextDocumentTemplate> get(int docId);

}
