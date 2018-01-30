package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for work with common document entities.
 * Every specified document type has it's own corresponding service.
 */
@Service
class DefaultDocumentService implements DocumentService<DocumentDTO> {

    private final MetaRepository metaRepository;
    private final TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> documentMapping;
    private final CommonContentService commonContentService;
    private final VersionService versionService;
    private final TextService textService;
    private final ImageService imageService;
    private final LoopService loopService;
    private final DocumentIndex documentIndex;
    private final List<VersionedContentService> versionedContentServices;
    private final Function<DocumentDTO, Meta> documentSaver;

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
                           List<VersionedContentService> versionedContentServices) {

        this.metaRepository = metaRepository;
        this.documentMapping = metaToDocumentDTO;
        this.commonContentService = commonContentService;
        this.versionService = versionService;
        this.textService = textService;
        this.imageService = imageService;
        this.loopService = loopService;
        this.documentIndex = documentIndex;
        this.versionedContentServices = versionedContentServices;
        this.documentSaver = ((Function<Meta, Meta>) metaRepository::save).compose(documentDtoToMeta);
    }

    @PostConstruct
    private void init() {
        docContentServices = new DeleterByDocumentId[]{
                textService,
                imageService,
                loopService,
                commonContentService,
                versionService
        };
    }

    @Override
    public DocumentDTO createEmpty() {
        final List<CommonContentDTO> commonContents = commonContentService.createCommonContents()
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toList());

        return Value.with(DocumentDTO.createEmpty(), documentDTO -> documentDTO.setCommonContents(commonContents));
    }

    @Override
    public DocumentDTO get(int docId) {
        final Version latestVersion = versionService.getLatestVersion(docId);
        final List<CommonContent> commonContents = commonContentService.getOrCreateCommonContents(
                docId, latestVersion.getNo()
        );
        return documentMapping.apply(metaRepository.findOne(docId), latestVersion, commonContents);
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
        final Integer docId = documentSaver.apply(saveMe).getId();

        if (isNew) {
            versionService.create(docId);
            saveMe.getCommonContents().forEach(commonContentDTO -> commonContentDTO.setDocId(docId));
        }

        commonContentService.save(docId, new ArrayList<>(saveMe.getCommonContents()));
        documentIndex.reindexDocument(docId);

        return docId;
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        if (!versionService.hasNewerVersion(docId)) {
            return false;
        }

        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        final Version newVersion = versionService.create(docId, userId);

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

}
