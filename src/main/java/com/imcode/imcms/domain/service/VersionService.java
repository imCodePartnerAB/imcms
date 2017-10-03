package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.service.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    private final VersionRepository versionRepository;

    public VersionService(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    Version getDocumentWorkingVersion(int docId) throws DocumentNotExistException {
        final Version workingVersion = versionRepository.findWorking(docId);

        if (workingVersion == null) {
            throw new DocumentNotExistException();
        }

        return workingVersion;
    }
}
