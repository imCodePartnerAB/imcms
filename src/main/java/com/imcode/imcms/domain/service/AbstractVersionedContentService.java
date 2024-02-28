package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.VersionedContentRepository;
import imcode.server.Imcms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class AbstractVersionedContentService<JPA, Repository extends VersionedContentRepository<JPA> & JpaRepository<JPA, Integer>>
        implements VersionedContentService {

    protected final Repository repository;

    @Autowired
    private VersionService versionService;

    protected AbstractVersionedContentService(Repository repository) {
        this.repository = repository;
    }

    protected abstract JPA removeId(JPA dto, Version version);

    @Override
    public void createVersionedContent(Version workingVersion, Version newVersion) {
        final List<JPA> forSave = repository.findByVersion(workingVersion)
                .stream()
                .map(jpa -> removeId(jpa, newVersion))
                .collect(toList());

        repository.saveAll(forSave);
    }

    protected void updateWorkingVersion(int docId) {
        versionService.updateWorkingVersion(docId);
    }

    protected void indexAndCacheActualization(int docId) {
        if (Imcms.isVersioningAllowed()) {
            Imcms.getServices().getDocumentMapper().getDocumentIndex().updateDocumentVersion(docId);
        } else {
            Imcms.getServices().getDocumentMapper().invalidateDocument(docId);
        }
    }
}
