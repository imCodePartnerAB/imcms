package com.imcode.imcms.mapping;


import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.mapping.container.DocVersionRef;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class DocumentVersionMapper {

    @Inject
    private VersionRepository versionRepository;

    public DocumentVersion get(DocVersionRef docVersionRef) {
        return toApiObject(versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo()));
    }

    public List<DocumentVersion> getAll(int docId) {
        List<Version> jpaVersions = versionRepository.findByDocId(docId);
        List<DocumentVersion> result = new LinkedList<>();
        for (Version jpaVersion : jpaVersions) {
            result.add(toApiObject(jpaVersion));
        }

        return result;
    }

    public DocumentVersion getDefault(int docId) {
        return toApiObject(versionRepository.findDefault(docId));
    }

    public DocumentVersion getWorking(int docId) {
        return toApiObject(versionRepository.findWorking(docId));
    }


    public DocumentVersionInfo getInfo(int docId) {
        List<DocumentVersion> versions = getAll(docId);

        if (versions.isEmpty()) {
            return null;
        }

        return new DocumentVersionInfo(docId, versions, getWorking(docId), getDefault(docId));
    }

    public DocumentVersion create(int docId, int userId) {
        return toApiObject(versionRepository.create(docId, userId));
    }


    private DocumentVersion toApiObject(Version jpaVersion) {
        return jpaVersion == null
                ? null
                : DocumentVersion.builder()
                .no(jpaVersion.getNo())
                .createdBy(jpaVersion.getCreatedBy().getId())
                .modifiedBy(jpaVersion.getModifiedBy().getId())
                .createdDt(jpaVersion.getCreatedDt())
                .modifiedDt(jpaVersion.getModifiedDt())
                .build();
    }
}
