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

import java.util.List;

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
        return getByDocIdAndVersionNo(docId, Version.WORKING_VERSION_INDEX);
    }

	@Override
	public List<? extends DocumentURL> getAllContainingInURL(String content) {
		return documentUrlRepository.findAllByUrlContains(content).stream().map(DocumentUrlDTO::new).toList();
	}

	@Override
    public DocumentURL getByDocIdAndVersionNo(int docId, int versionNo){
        final DocumentUrlJPA documentUrlJPA = documentUrlRepository.findByDocIdAndVersionNo(docId, versionNo);
        return documentUrlJPA != null ? new DocumentUrlDTO(documentUrlJPA) : null;
    }

    @Override
    public void setAsWorkingVersion(Version version) {
        Version workingVersion = versionService.getDocumentWorkingVersion(version.getDocId());

        final DocumentUrlJPA documentUrlByVersion = documentUrlRepository.findByDocIdAndVersionNo(version.getDocId(), version.getNo());

        final DocumentUrlJPA saveDocumentUrl = new DocumentUrlJPA(documentUrlByVersion, workingVersion);
        saveDocumentUrl.setId(null);

        documentUrlRepository.deleteByVersion(workingVersion);
        documentUrlRepository.flush();
        documentUrlRepository.save(saveDocumentUrl);
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
