package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VersionService {

    private final VersionRepository versionRepository;

    public VersionService(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    public Version getDocumentWorkingVersion(int docId) {
        return Optional.ofNullable(versionRepository.findWorking(docId)).orElseThrow(DocumentNotExistException::new);
    }
}
