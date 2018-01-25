package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.Value;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;

@Component
public class DocumentDataInitializer extends TestDataCleaner {

    private static final int TEST_VERSION_INDEX = 0;
    private static final int DEFAULT_DOC_ID = 1001;

    private final MetaRepository metaRepository;
    private final DocumentFileRepository documentFileRepository;
    private final TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO;
    private final CommonContentDataInitializer commonContentDataInitializer;
    private final VersionDataInitializer versionDataInitializer;
    private final TemplateDataInitializer templateDataInitializer;
    private final CommonContentService commonContentService;
    private final DocumentUrlRepository documentUrlRepository;

    public DocumentDataInitializer(MetaRepository metaRepository,
                                   DocumentFileRepository documentFileRepository,
                                   TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                                   VersionDataInitializer versionDataInitializer,
                                   TemplateDataInitializer templateDataInitializer,
                                   CommonContentService commonContentService,
                                   CommonContentDataInitializer commonContentDataInitializer,
                                   DocumentUrlRepository documentUrlRepository) {

        this.metaRepository = metaRepository;
        this.documentFileRepository = documentFileRepository;
        this.metaToDocumentDTO = metaToDocumentDTO;
        this.versionDataInitializer = versionDataInitializer;
        this.templateDataInitializer = templateDataInitializer;
        this.commonContentService = commonContentService;
        this.commonContentDataInitializer = commonContentDataInitializer;
        this.documentUrlRepository = documentUrlRepository;
    }

    public DocumentDTO createData(Meta.DocumentType type) {
        final Meta metaDoc = Value.with(new Meta(), meta -> {

            meta.setArchivedDatetime(new Date());
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
            meta.setPublicationStartDatetime(new Date());
            meta.setPublicationStatus(Meta.PublicationStatus.APPROVED);
            meta.setPublisherId(1);
            meta.setSearchDisabled(false);
            meta.setTarget("test");

        });

        metaRepository.save(metaDoc);

        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, metaDoc.getId());
        commonContentDataInitializer.createData(metaDoc.getId(), TEST_VERSION_INDEX);

        final List<CommonContent> commonContents = commonContentService.getOrCreateCommonContents(
                metaDoc.getId(), version.getNo()
        );
        return metaToDocumentDTO.apply(metaDoc, version, commonContents);
    }

    public DocumentDTO createData() {
        return createData(Meta.DocumentType.TEXT);
    }

    public FileDocumentDTO createFileDocument() {
        final DocumentDTO documentDTO = createData(Meta.DocumentType.FILE);
        final FileDocumentDTO fileDocumentDTO = new FileDocumentDTO(documentDTO);

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(documentDTO.getId());
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id");
        documentFileJPA.setFilename("test_name");
        documentFileJPA.setMimeType("test");

        final List<DocumentFileDTO> documentFileDTOS = new ArrayList<>();
        documentFileDTOS.add(new DocumentFileDTO(documentFileRepository.save(documentFileJPA)));
        fileDocumentDTO.setFiles(documentFileDTOS);

        return fileDocumentDTO;
    }

    public TextDocumentDTO createTextDocument() {
        templateDataInitializer.cleanRepositories();
        final DocumentDTO documentDTO = createData(Meta.DocumentType.TEXT);
        final TextDocumentTemplateJPA template = templateDataInitializer.createData(
                documentDTO.getId(), "demo", "demo"
        );
        final TextDocumentDTO textDocumentDTO = new TextDocumentDTO(documentDTO);
        textDocumentDTO.setTemplate(new TextDocumentTemplateDTO(template));

        return textDocumentDTO;
    }

    public UrlDocumentDTO createUrlDocument() {
        final DocumentDTO documentDTO = createData(Meta.DocumentType.URL);
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(documentDTO);

        DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl("test");
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");

        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, DEFAULT_DOC_ID);
        documentUrlJPA.setVersion(version);

        final DocumentUrlDTO documentUrlDTO =
                new DocumentUrlDTO(documentUrlRepository.saveAndFlush(documentUrlJPA));

        urlDocumentDTO.setDocumentUrlDTO(documentUrlDTO);

        return urlDocumentDTO;
    }

    public void cleanRepositories(int createdDocId) {
        Imcms.removeUser();
        templateDataInitializer.cleanRepositories();
        commonContentDataInitializer.cleanRepositories();
        metaRepository.delete(createdDocId);
        versionDataInitializer.cleanRepositories();
    }
}
