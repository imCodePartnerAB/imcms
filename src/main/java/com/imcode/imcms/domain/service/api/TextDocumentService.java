package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.util.Value;
import imcode.server.document.index.DocumentIndex;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for work with Text Documents only.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
@Service
@Transactional
public class TextDocumentService implements DocumentService<TextDocumentDTO> {

    private final static Logger LOGGER = Logger.getLogger(TextDocumentService.class);


    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final TextDocumentTemplateService textDocumentTemplateService;
    private final TextService textService;
    private final ImageService imageService;

    public TextDocumentService(DocumentService<DocumentDTO> documentService,
                               TextDocumentTemplateService textDocumentTemplateService,
                               ImageService imageService,
                               TextService textService) {

        this.defaultDocumentService = documentService;
        this.textDocumentTemplateService = textDocumentTemplateService;
        this.imageService = imageService;
        this.textService = textService;
    }

    @Override
    public long countDocuments() {
        return defaultDocumentService.countDocuments();
    }

    @Override
    public TextDocumentDTO createFromParent(Integer parentDocId) {
        TextDocumentDTO parentClone = get(parentDocId).clone();
        parentClone.setLatestVersion(parentClone.getCurrentVersion());
        return Value.with(parentClone, this::swapTemplateNames);
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

        final DocumentDTO savedDoc = defaultDocumentService.save(saveMe);
        final int savedDocId = savedDoc.getId();

        if (isNew) {
            oTemplate.ifPresent(textDocumentTemplateDTO -> textDocumentTemplateDTO.setDocId(savedDocId));
            for (CommonContent content : savedDoc.getCommonContents()) {
                final Text text = textService.getText(savedDocId, 1, content.getLanguage().getCode(), null);
                text.setText(content.getHeadline());
                textService.save(text);
            }
        }

        oTemplate.ifPresent(textDocumentTemplateService::save);

        return saveMe;
    }

    @Override
    public TextDocumentDTO copy(int docId) {
        final int copiedDocId = defaultDocumentService.copy(docId).getId();

        textDocumentTemplateService.copy(docId, copiedDocId);

        return get(copiedDocId);
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public SolrInputDocument index(int docId) {

        LOGGER.debug(String.format("Start index doc id %d", docId));
        final TextDocumentDTO doc = get(docId);
        LOGGER.debug(String.format("Got doc id %d", docId));

        final SolrInputDocument solrInputDocument = defaultDocumentService.index(docId);

        solrInputDocument.addField(DocumentIndex.FIELD__TEMPLATE, doc.getTemplate());
        LOGGER.debug(String.format("Add field Template in Text-Doc id %d, with template %s", doc.getId(), doc.getTemplate().getTemplateName()));

        doc.getCommonContents()
                .forEach(commonContentDTO -> {
                    final Language language = commonContentDTO.getLanguage();

                    textService.getPublicTexts(docId, language).forEach(indexMe -> {
                        final String textValue = indexMe.getText();

                        final Integer textIndex = Optional.ofNullable(indexMe.getLoopEntryRef())
                                .map(LoopEntryRef::getLoopEntryIndex)
                                .orElseGet(indexMe::getIndex);

                        solrInputDocument.addField(DocumentIndex.FIELD__TEXT, textValue);
                        LOGGER.info(String.format("Add text content %s with id-doc %d and with text-index %d", textValue, doc.getId(), textIndex));
                        solrInputDocument.addField(DocumentIndex.FIELD__TEXT + textIndex, textValue);
                    });

                    imageService.getPublicImageLinks(docId, language).forEach(
                            imageLinkUrl -> solrInputDocument.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl)
                    );
                });

        LOGGER.info(String.format("Finished add index in Textdoc %d", doc.getId()));
        return solrInputDocument;
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        textDocumentTemplateService.deleteByDocId(docIdToDelete);
        defaultDocumentService.deleteByDocId(docIdToDelete);
    }

    private void swapTemplateNames(TextDocumentDTO document) {
        final TextDocumentTemplateDTO template = document.getTemplate();
        template.setTemplateName(template.getChildrenTemplateName());
    }

    @Override
    public List<TextDocumentDTO> getDocumentsByTemplateName(String templateName) {
        return null;
    }

    @Override
    public String getUniqueAlias(String alias) {
        return defaultDocumentService.getUniqueAlias(alias);
    }

}
