package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.Value;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;

@Component
public class DocumentDataInitializer extends TestDataCleaner {

    static final int TEST_VERSION_INDEX = 0;

    private final MetaRepository metaRepository;
    private final TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO;
    private final CommonContentDataInitializer commonContentDataInitializer;
    private final VersionDataInitializer versionDataInitializer;

    public DocumentDataInitializer(MetaRepository metaRepository,
                                   TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                                   VersionDataInitializer versionDataInitializer,
                                   CommonContentDataInitializer commonContentDataInitializer) {

        this.metaRepository = metaRepository;
        this.metaToDocumentDTO = metaToDocumentDTO;
        this.versionDataInitializer = versionDataInitializer;
        this.commonContentDataInitializer = commonContentDataInitializer;
    }

    protected DocumentDTO createData(Meta.DocumentType type, Meta.PublicationStatus status) {
        final Meta metaDoc = Value.with(new Meta(), meta -> {

            meta.setArchiverId(1);
            meta.setCategories(new HashSet<>());
            meta.setCreatedDatetime(new Date());
            meta.setCreatorId(1);
            meta.setModifiedDatetime(new Date());
            meta.setModifierId(1);
            meta.setDefaultVersionNo(0);
            meta.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
            meta.setDocumentType(type);
            meta.setKeywords(new HashSet<>());
            meta.setLinkableByOtherUsers(true);
            meta.setLinkedForUnauthorizedUsers(true);
            meta.setPublicationStatus(status);
            meta.setPublisherId(1);
            meta.setSearchDisabled(false);
            meta.setTarget("test");
        });

        metaRepository.save(metaDoc);

        final Integer docId = metaDoc.getId();
        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, docId);
        final int versionIndex = version.getNo();
        final List<CommonContent> commonContents = commonContentDataInitializer.createData(docId, versionIndex);

        final DocumentDTO documentDTO = metaToDocumentDTO.apply(metaDoc, version, commonContents);
        documentDTO.setLatestVersion(AuditDTO.fromVersion(version));

        return documentDTO;
    }

    protected DocumentDTO createData(Meta.DocumentType type) {
        return createData(type, Meta.PublicationStatus.APPROVED);
    }

    public DocumentDTO createData() {
        return createData(Meta.DocumentType.TEXT, Meta.PublicationStatus.APPROVED);
    }

    public DocumentDTO createData(Meta.PublicationStatus status) {
        return createData(Meta.DocumentType.TEXT, status);
    }

    public void cleanRepositories(int createdDocId) {
        Imcms.removeUser();
        commonContentDataInitializer.cleanRepositories();
        metaRepository.delete(createdDocId);
        versionDataInitializer.cleanRepositories();
    }
}
