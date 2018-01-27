package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultDocumentUrlService
        extends AbstractVersionedContentService<DocumentUrlJPA, DocumentURL, DocumentUrlRepository>
        implements DocumentUrlService {

    private final VersionService versionService;
    private final DocumentUrlRepository documentUrlRepository;

    public DefaultDocumentUrlService(VersionService versionService,
                                     DocumentUrlRepository documentUrlRepository) {

        super(documentUrlRepository);
        this.versionService = versionService;
        this.documentUrlRepository = documentUrlRepository;
    }

    @Override
    public DocumentURL getByDocId(int docId) {
        final DocumentUrlJPA documentUrlJPA = documentUrlRepository.findByDocIdAndVersionNo(
                docId, Version.WORKING_VERSION_INDEX
        );

        return new DocumentUrlDTO(documentUrlJPA);
    }

    @Override
    public DocumentURL save(DocumentURL documentURL) {

        Version documentWorkingVersion;
        try {
            documentWorkingVersion = versionService.getDocumentWorkingVersion(documentURL.getDocId());

        } catch (DocumentNotExistException e) {
            documentWorkingVersion = versionService.create(documentURL.getDocId());
        }

        final DocumentUrlJPA documentUrlJPA = documentUrlRepository.saveAndFlush(
                new DocumentUrlJPA(documentURL, documentWorkingVersion)
        );

        updateWorkingVersion(documentURL.getDocId());

        return new DocumentUrlDTO(documentUrlJPA);
    }

    @Override
    protected DocumentURL mapToDTO(DocumentUrlJPA documentUrlJPA) {
        return new DocumentUrlDTO(documentUrlJPA);
    }

    @Override
    protected DocumentUrlJPA mapToJpaWithoutId(DocumentURL documentURL, Version version) {
        final DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA(documentURL, version);
        documentUrlJPA.setId(null);

        return documentUrlJPA;
    }
}
