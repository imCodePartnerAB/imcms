package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.Value;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.document.index.DocumentIndex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class DefaultDocumentService implements DocumentService {

    private final MetaRepository metaRepository;
    private final TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> documentMapping;
    private final Function<DocumentDTO, Meta> documentDtoToMeta;
    private final CommonContentService commonContentService;
    private final VersionService versionService;
    private final TextService textService;
    private final ImageService imageService;
    private final LoopService loopService;
    private final DocumentIndex documentIndex;
    private final TextDocumentTemplateService textDocumentTemplateService;
    private final List<VersionedContentService> versionedContentServices;

    private DeleterByDocumentId[] docContentServices = {};

    DefaultDocumentService(MetaRepository metaRepository,
                           TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                           Function<DocumentDTO, Meta> documentDtoToMeta,
                           CommonContentService commonContentService,
                           VersionService versionService,
                           TextService textService,
                           ImageService imageService,
                           LoopService loopService,
                           DocumentIndex documentIndex,
                           TextDocumentTemplateService textDocumentTemplateService,
                           List<VersionedContentService> versionedContentServices) {

        this.metaRepository = metaRepository;
        this.documentMapping = metaToDocumentDTO;
        this.documentDtoToMeta = documentDtoToMeta;
        this.commonContentService = commonContentService;
        this.versionService = versionService;
        this.textService = textService;
        this.imageService = imageService;
        this.loopService = loopService;
        this.documentIndex = documentIndex;
        this.textDocumentTemplateService = textDocumentTemplateService;
        this.versionedContentServices = versionedContentServices;
    }

    @PostConstruct
    private void init() {
        docContentServices = new DeleterByDocumentId[]{
                textService,
                imageService,
                loopService,
                textDocumentTemplateService,
                commonContentService,
                versionService
        };
    }

    @Override
    public DocumentDTO get(Integer docId) {
        return (docId == null) ? buildNewDocument() : getDocument(docId);
    }

    /**
     * Saves document to DB.
     *
     * @param saveMe document to be saved
     * @return id of saved document
     */
    @Override
    @Transactional
    public int save(DocumentDTO saveMe) {
        final boolean isNew = (saveMe.getId() == null);

        final Optional<TextDocumentTemplateDTO> oTemplate = Optional.ofNullable(saveMe.getTemplate());
        final Meta metaForSave = documentDtoToMeta.apply(saveMe);
        final Integer docId = metaRepository.save(metaForSave).getId();

        if (isNew) {
            versionService.create(docId);
            oTemplate.ifPresent(textDocumentTemplateDTO -> textDocumentTemplateDTO.setDocId(docId));
            saveMe.getCommonContents().forEach(commonContentDTO -> commonContentDTO.setDocId(docId));
        }

        commonContentService.save(new ArrayList<>(saveMe.getCommonContents()));
        oTemplate.ifPresent(textDocumentTemplateService::save);

        documentIndex.indexDocument(docId);

        return docId;
    }

    @Override
    @Transactional
    public void delete(DocumentDTO deleteMe) {
        deleteByDocId(deleteMe.getId());
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        if (!versionService.hasNewerVersion(docId)) {
            return false;
        }

        final Version workingVersion = versionService.getDocumentWorkingVersion(docId),
                newVersion = versionService.create(docId, userId);

        versionedContentServices.forEach(vcs -> vcs.createVersionedContent(workingVersion, newVersion));

        return true;
    }

    @Override
    @Transactional
    public void deleteByDocId(Integer docIdToDelete) {
        deleteDocumentContent(docIdToDelete);
        metaRepository.delete(docIdToDelete);
        documentIndex.removeDocument(docIdToDelete);
    }

    @Transactional
    protected void deleteDocumentContent(Integer docIdToDelete) {
        for (DeleterByDocumentId docContentService : docContentServices) {
            docContentService.deleteByDocId(docIdToDelete);
        }
    }

    private DocumentDTO buildNewDocument() {
        final List<CommonContentDTO> commonContents = commonContentService.createCommonContents()
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toList());

        return Value.with(DocumentDTO.createNew(), documentDTO -> documentDTO.setCommonContents(commonContents));
    }

    private DocumentDTO getDocument(int docId) {
        final Version latestVersion = versionService.getLatestVersion(docId);
        final List<CommonContent> commonContents = commonContentService.getOrCreateCommonContents(
                docId, latestVersion.getNo()
        );
        return documentMapping.apply(metaRepository.findOne(docId), latestVersion, commonContents);
    }
}
