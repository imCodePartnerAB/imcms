package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import imcode.server.document.index.DocumentIndex;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for work with Text Documents only.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
@Transactional
public class TextDocumentService implements DocumentService<TextDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentDtoFactory documentDtoFactory;
    private final TextDocumentTemplateService textDocumentTemplateService;
    private TextService textService;
    private ImageService imageService;

    public TextDocumentService(DocumentService<DocumentDTO> documentService,
                               DocumentDtoFactory documentDtoFactory,
                               TextDocumentTemplateService textDocumentTemplateService,
                               ImageService imageService,
                               TextService textService) {

        this.defaultDocumentService = documentService;
        this.documentDtoFactory = documentDtoFactory;
        this.textDocumentTemplateService = textDocumentTemplateService;
        this.imageService = imageService;
        this.textService = textService;
    }

    @Override
    public TextDocumentDTO createFromParent(Integer parentDocId) { // todo: use copying to create new doc based on parent
        return documentDtoFactory.createEmptyTextDocument();
    }

    @Override
    public TextDocumentDTO get(int docId) {
        final TextDocumentDTO textDocDTO = new TextDocumentDTO(defaultDocumentService.get(docId));
        textDocumentTemplateService.get(docId).map(TextDocumentTemplateDTO::new).ifPresent(textDocDTO::setTemplate);
        return textDocDTO;
    }

    @Override
    public TextDocumentDTO save(TextDocumentDTO saveMe) {
        final boolean isNew = (saveMe.getId() == null);
        final Optional<TextDocumentTemplateDTO> oTemplate = Optional.ofNullable(saveMe.getTemplate());

        final int savedDocId = defaultDocumentService.save(saveMe).getId();

        if (isNew) {
            oTemplate.ifPresent(textDocumentTemplateDTO -> textDocumentTemplateDTO.setDocId(savedDocId));
        }

        oTemplate.ifPresent(textDocumentTemplateService::save);

        return saveMe;
    }

    @Override
    public TextDocumentDTO copy(int docId) {
        final TextDocumentDTO clonedTextDocumentDTO = get(docId).clone();

        save(clonedTextDocumentDTO);

        return get(clonedTextDocumentDTO.getId());
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public SolrInputDocument index(int docId) {

        final TextDocumentDTO doc = get(docId);

        final SolrInputDocument solrInputDocument = defaultDocumentService.index(docId);

        solrInputDocument.addField(DocumentIndex.FIELD__TEMPLATE, doc.getTemplate());

        doc.getCommonContents()
                .forEach(commonContentDTO -> {
                    final Language language = commonContentDTO.getLanguage();

                    textService.getPublicTexts(docId, language).forEach(indexMe -> {
                        final String textValue = indexMe.getText();

                        final Integer textIndex = Optional.ofNullable(indexMe.getLoopEntryRef())
                                .map(LoopEntryRef::getLoopEntryIndex)
                                .orElseGet(indexMe::getIndex);

                        solrInputDocument.addField(DocumentIndex.FIELD__TEXT, textValue);
                        solrInputDocument.addField(DocumentIndex.FIELD__TEXT + textIndex, textValue);
                    });

                    imageService.getPublicImageLinks(docId, language).forEach(
                            imageLinkUrl -> solrInputDocument.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl)
                    );
                });

        return solrInputDocument;
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        textDocumentTemplateService.deleteByDocId(docIdToDelete);
        defaultDocumentService.deleteByDocId(docIdToDelete);
    }
}
