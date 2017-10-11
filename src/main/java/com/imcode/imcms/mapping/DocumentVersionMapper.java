package com.imcode.imcms.mapping;


import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class DocumentVersionMapper {

    @Inject
    private VersionRepository versionRepository;

    @Autowired
    private VersionService versionService;

    public DocumentVersion get(VersionRef versionRef) {
        return toApiObject(versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo()));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<DocumentVersion> getAll(int docId) {
        List<Version> versions = versionRepository.findByDocId(docId);
        List<DocumentVersion> result = new LinkedList<>();
        for (Version version : versions) {
            result.add(toApiObject(version));
        }

        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public DocumentVersion getDefault(int docId) {
        return toApiObject(versionRepository.findDefault(docId));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public DocumentVersion getWorking(int docId) {
        return toApiObject(versionRepository.findWorking(docId));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public DocumentVersionInfo getInfo(int docId) {
        List<DocumentVersion> versions = getAll(docId);

        if (versions.isEmpty()) {
            return null;
        }

        return new DocumentVersionInfo(docId, versions, getWorking(docId), getDefault(docId));
    }

    /**
     * Creates and returns a new version of a document.
     * If document does not have version creates version with number 0 otherwise creates version with next version number.
     *
     * @return new document version.
     */
    public DocumentVersion create(int docId, int userId) {
        return toApiObject(versionService.create(docId, userId));
    }


    private DocumentVersion toApiObject(Version version) {
        return version == null
                ? null
                : DocumentVersion.builder()
                .no(version.getNo())
                .createdBy(version.getCreatedBy().getId())
                .modifiedBy(version.getModifiedBy().getId())
                .createdDt(version.getCreatedDt())
                .modifiedDt(version.getModifiedDt())
                .build();
    }
}
