package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentUrlDTO;
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
        extends AbstractVersionedContentService<DocumentUrlJPA, DocumentUrlRepository>
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

        return documentUrlJPA != null ? new DocumentUrlDTO(documentUrlJPA) : null;
    }

    @Override
    public DocumentURL save(DocumentURL documentURL) {

        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(documentURL.getDocId());

        final DocumentUrlJPA documentUrlJPA = documentUrlRepository.saveAndFlush(
                new DocumentUrlJPA(documentURL, documentWorkingVersion)
        );

        updateWorkingVersion(documentURL.getDocId());

        return new DocumentUrlDTO(documentUrlJPA);
    }

    @Override
    public void copy(int fromDocId, int toDocId) {
        final DocumentUrlDTO originalDocumentUrlDTO = new DocumentUrlDTO(getByDocId(fromDocId));

        final DocumentUrlDTO clonedDocumentUrlDTO = originalDocumentUrlDTO.clone();
        clonedDocumentUrlDTO.setDocId(toDocId);

        save(clonedDocumentUrlDTO);
    }

    @Override
    protected DocumentUrlJPA removeId(DocumentUrlJPA documentURL, Version version) {
        final DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA(documentURL, version);
        documentUrlJPA.setId(null);

        return documentUrlJPA;
    }
}
