package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentValidatingService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.persistence.entity.Meta;
import org.springframework.stereotype.Service;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.02.18.
 */
@Service
public class DefaultDocumentValidatingService implements DocumentValidatingService {

    private final DocumentMapper documentMapper;
    private final DocumentService<DocumentDTO> documentService;

    public DefaultDocumentValidatingService(DocumentMapper documentMapper,
                                            DocumentService<DocumentDTO> documentService) {
        this.documentMapper = documentMapper;
        this.documentService = documentService;
    }

    @Override
    public boolean isTextDocument(String documentIdentifier) {
        final Integer documentId = documentMapper.toDocumentId(documentIdentifier);

        return (documentId != null) && (Meta.DocumentType.TEXT == documentService.get(documentId).getType());
    }

}
