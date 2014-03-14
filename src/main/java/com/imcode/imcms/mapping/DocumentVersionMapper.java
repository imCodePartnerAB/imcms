package com.imcode.imcms.mapping;


import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class DocumentVersionMapper {

    @Inject
    private VersionRepository versionRepository;

    public DocumentVersion get(VersionRef versionRef) {
        return toApiObject(versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo()));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DocumentVersion> getAll(int docId) {
        List<Version> versions = versionRepository.findByDocId(docId);
        List<DocumentVersion> result = new LinkedList<>();
        for (Version version : versions) {
            result.add(toApiObject(version));
        }

        return result;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public DocumentVersion getDefault(int docId) {
        return toApiObject(versionRepository.findDefault(docId));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public DocumentVersion getWorking(int docId) {
        return toApiObject(versionRepository.findWorking(docId));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
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
        return toApiObject(versionRepository.create(docId, userId));
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
