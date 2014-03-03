package com.imcode.imcms.mapping;


import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.DocVersionRef;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

//fixme: implement
@Service
@Transactional
public class DocumentVersionMapper {

    @Inject
    private DocVersionRepository versionRepository;

    public List<DocumentVersion> getAll(int docId) {
        throw new NotImplementedException();
    }

    public DocumentVersion getDefault(int docId) {
        throw new NotImplementedException();
    }

    public DocumentVersion get(DocVersionRef docVersionRef) {
        throw new NotImplementedException();
    }

    public void getDefault(int docId, DocumentVersion documentVersion) {
        throw new NotImplementedException();
    }

    public DocumentVersion toApiObject(DocVersion jpaVersion) {
        return DocumentVersion.builder()
                .no(jpaVersion.getNo())
                .createdBy(jpaVersion.getCreatedBy().getId())
                .modifiedBy(jpaVersion.getModifiedBy().getId())
                .createdDt(jpaVersion.getCreatedDt())
                .modifiedDt(jpaVersion.getModifiedDt())
                .build();
    }
}
