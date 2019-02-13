package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.TextDocumentTemplate;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import com.imcode.imcms.persistence.repository.TextRepository;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.util.ImcmsImageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class TextDocumentContentLoader {

    private final VersionRepository versionRepository;
    private final TextRepository textRepository;
    private final TextHistoryRepository textHistoryRepository;
    private final ImageRepository imageRepository;
    private final TextDocumentTemplateRepository textDocumentTemplateRepository;
    private final LanguageRepository languageRepository;
    private final DocumentLanguageMapper languageMapper;
    private final LoopService loopService;

    public TextDocumentContentLoader(VersionRepository versionRepository,
                                     TextRepository textRepository,
                                     TextHistoryRepository textHistoryRepository,
                                     ImageRepository imageRepository,
                                     TextDocumentTemplateRepository textDocumentTemplateRepository,
                                     LanguageRepository languageRepository,
                                     DocumentLanguageMapper languageMapper,
                                     LoopService loopService) {

        this.versionRepository = versionRepository;
        this.textRepository = textRepository;
        this.textHistoryRepository = textHistoryRepository;
        this.imageRepository = imageRepository;
        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
        this.loopService = loopService;
    }


    TextDocumentDomainObject.TemplateNames getTemplateNames(int docId) {
        TextDocumentTemplate jpaTextDocumentTemplate = textDocumentTemplateRepository.findOne(docId);

        if (jpaTextDocumentTemplate == null) return null;

        TextDocumentDomainObject.TemplateNames templateNamesDO = new TextDocumentDomainObject.TemplateNames();

        templateNamesDO.setDefaultTemplateName(jpaTextDocumentTemplate.getChildrenTemplateName());
        templateNamesDO.setTemplateName(jpaTextDocumentTemplate.getTemplateName());

        return templateNamesDO;
    }

    public Map<Integer, TextDomainObject> getTexts(final DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());

        return textRepository.findByVersionAndLanguageWhereLoopEntryRefIsNull(version, language)
                .stream().collect(toMap(TextJPA::getIndex, this::toDomainObject, (textJPA1, textJPA2) -> textJPA1));
    }

    public Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> getLoopTexts(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());

        final Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> result = new HashMap<>();

        for (TextJPA text : textRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    text.getLoopEntryRef().getLoopIndex(), text.getLoopEntryRef().getLoopEntryIndex(), text.getIndex()
            );

            result.put(loopItemRef, toDomainObject(text));
        }

        return result;
    }

    /**
     * Return text history based on special document {@link Version}, {@link LanguageJPA}, and text id
     *
     * @param docRef {@link DocRef} item
     * @param textNo text id
     * @return {@link Set<TextHistoryJPA>} of text history
     * @see Version
     * @see LanguageJPA
     * @see DocRef
     * @see imcode.server.document.DocumentDomainObject
     */
    public Collection<TextHistoryJPA> getTextHistory(DocRef docRef, int textNo) {
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());

        return textHistoryRepository.findTextHistoryNotInLoop(docRef.getId(), language, textNo);
    }

    /**
     * Return text history based on special document {@link Version}, {@link LanguageJPA},{@link LoopEntryRefJPA} and text id
     *
     * @param docRef {@link DocRef} item
     * @param textNo text id
     * @return {@link Collection<TextHistoryJPA>} of text history
     * @see Version
     * @see LanguageJPA
     * @see DocRef
     * @see LoopEntryRefJPA
     * @see imcode.server.document.DocumentDomainObject
     */
    public Collection<TextHistoryJPA> getTextHistory(DocRef docRef, LoopEntryRefJPA loopEntryRef, int textNo) {
        if (loopEntryRef == null) {
            return getTextHistory(docRef, textNo);
        }

        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());

        return textHistoryRepository.findTextHistoryInLoop(docRef.getId(), language, loopEntryRef, textNo);
    }


    public TextDomainObject getText(DocRef docRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());

        return toDomainObject(
                textRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, textNo)
        );
    }

    public Map<Integer, ImageDomainObject> getImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());

        return imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNull(version, language)
                .stream().collect(toMap(Image::getIndex, ImcmsImageUtils::toDomainObject));

    }

    public Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> getLoopImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());
        final Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    image.getLoopEntryRef().getLoopIndex(), image.getLoopEntryRef().getLoopEntryIndex(), image.getIndex()
            );

            result.put(loopItemRef, ImcmsImageUtils.toDomainObject(image));
        }

        return result;
    }

    public Map<DocumentLanguage, ImageDomainObject> getImages(VersionRef versionRef, int imageNo, com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef) {
        return (null == loopEntryRef) ? getImages(versionRef, imageNo)
                : getLoopImages(versionRef, TextDocumentDomainObject.LoopItemRef.of(loopEntryRef, imageNo));
    }

    public Map<DocumentLanguage, ImageDomainObject> getImages(VersionRef versionRef, int imageNo) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());
        Map<DocumentLanguage, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByVersionAndIndexWhereLoopEntryRefIsNull(version, imageNo)) {
            result.put(languageMapper.toApiObject(image.getLanguage()), ImcmsImageUtils.toDomainObject(image));
        }

        return result;
    }

    public Map<DocumentLanguage, ImageDomainObject> getLoopImages(VersionRef versionRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());
        Map<DocumentLanguage, ImageDomainObject> result = new HashMap<>();
        LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Image image : imageRepository.findByVersionAndIndexAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(languageMapper.toApiObject(image.getLanguage()), ImcmsImageUtils.toDomainObject(image));
        }

        return result;
    }

    public ImageDomainObject getImage(DocRef docRef, int imageNo, com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef) {
        return (loopEntryRef == null) ? getImage(docRef, imageNo)
                : getLoopImage(docRef, TextDocumentDomainObject.LoopItemRef.of(loopEntryRef, imageNo));
    }

    public ImageDomainObject getImage(DocRef docRef, int imageNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());

        return ImcmsImageUtils.toDomainObject(
                imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, imageNo)
        );
    }

    public ImageDomainObject getLoopImage(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        LanguageJPA language = languageRepository.findByCode(docRef.getLanguageCode());
        LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return ImcmsImageUtils.toDomainObject(
                imageRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }

    public Map<Integer, Loop> getLoops(VersionRef versionRef) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());

        return loopService.getByVersion(version).stream().collect(toMap(Loop::getIndex, loop -> loop));
    }


    private TextDomainObject toDomainObject(TextJPA jpaText) {
        return (jpaText == null) ? null : new TextDomainObject(jpaText.getText(), jpaText.getType().ordinal());
    }

}
