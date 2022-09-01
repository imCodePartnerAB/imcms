package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;

@Component
public class DocumentDataInitializer extends TestDataCleaner {

    static final int TEST_VERSION_INDEX = 0;

    private final MetaRepository metaRepository;
    private final BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO;
    private final CommonContentDataInitializer commonContentDataInitializer;
    private final VersionDataInitializer versionDataInitializer;

    public DocumentDataInitializer(MetaRepository metaRepository,
                                   BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                                   VersionDataInitializer versionDataInitializer,
                                   CommonContentDataInitializer commonContentDataInitializer) {

        this.metaRepository = metaRepository;
        this.metaToDocumentDTO = metaToDocumentDTO;
        this.versionDataInitializer = versionDataInitializer;
        this.commonContentDataInitializer = commonContentDataInitializer;
    }

    protected DocumentDTO createData(Meta.DocumentType type, Meta.PublicationStatus status,
                                     boolean isEnabledEngContent, boolean isEnabledSweContent) {
        final Meta metaDoc = Value.with(new Meta(), meta -> {

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
            meta.setVisible(true);
            meta.setPublicationStatus(status);
            meta.setSearchDisabled(false);
            meta.setTarget("test");
        });

        metaRepository.save(metaDoc);

        final Integer docId = metaDoc.getId();
        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, docId);
        final int versionIndex = version.getNo();
        final List<CommonContent> commonContents = commonContentDataInitializer.createData(
                docId, versionIndex, isEnabledEngContent, isEnabledSweContent
        );

        final DocumentDTO documentDTO = metaToDocumentDTO.apply(metaDoc, commonContents);
        documentDTO.setLatestVersion(new AuditDTO(version.getNo(), version.getCreatedBy().getLogin(), version.getCreatedDt()));

        return documentDTO;
    }

    protected DocumentDTO createData(Meta.DocumentType type) {
        return createData(type, Meta.PublicationStatus.APPROVED, true, true);
    }

    public DocumentDTO createData() {
        return createData(Meta.DocumentType.TEXT, Meta.PublicationStatus.APPROVED, true, true);
    }

    public List<DocumentDTO> createDocumentsData(Integer count, boolean isEnabledEngContent, boolean isEnabledSweContent) {
        final List<DocumentDTO> newDocuments = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            newDocuments.add(createData(Meta.DocumentType.TEXT, Meta.PublicationStatus.APPROVED, isEnabledEngContent, isEnabledSweContent));
        }

        return newDocuments;
    }

    public DocumentDTO createData(Meta.PublicationStatus status, boolean isEnabledEngContent, boolean isEnabledSweContent ) {
        return createData(Meta.DocumentType.TEXT, status, isEnabledEngContent, isEnabledSweContent);
    }

    public void cleanRepositories(int createdDocId) {
        Imcms.removeUser();
        commonContentDataInitializer.cleanRepositories();
	    metaRepository.deleteById(createdDocId);
	    versionDataInitializer.cleanRepositories();
    }
}
