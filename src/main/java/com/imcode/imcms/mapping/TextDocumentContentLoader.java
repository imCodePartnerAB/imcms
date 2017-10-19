package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.LoopRepository;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class TextDocumentContentLoader {

    private final VersionRepository versionRepository;
    private final TextRepository textRepository;
    private final TextHistoryRepository textHistoryRepository;
    private final ImageRepository imageRepository;
    private final MenuRepository menuRepository;
    private final TemplateNamesRepository templateNamesRepository;
    private final LoopRepository loopRepository;
    private final LanguageRepository languageRepository;
    private final IncludeRepository includeRepository;
    private final DocumentGetter menuItemDocumentGetter;
    private final DocumentLanguageMapper languageMapper;

    @Inject
    public TextDocumentContentLoader(VersionRepository versionRepository, TextRepository textRepository,
                                     TextHistoryRepository textHistoryRepository, ImageRepository imageRepository,
                                     MenuRepository menuRepository, TemplateNamesRepository templateNamesRepository,
                                     LoopRepository loopRepository, LanguageRepository languageRepository,
                                     IncludeRepository includeRepository, DocumentGetter menuItemDocumentGetter,
                                     DocumentLanguageMapper languageMapper) {
        this.versionRepository = versionRepository;
        this.textRepository = textRepository;
        this.textHistoryRepository = textHistoryRepository;
        this.imageRepository = imageRepository;
        this.menuRepository = menuRepository;
        this.templateNamesRepository = templateNamesRepository;
        this.loopRepository = loopRepository;
        this.languageRepository = languageRepository;
        this.includeRepository = includeRepository;
        this.menuItemDocumentGetter = menuItemDocumentGetter;
        this.languageMapper = languageMapper;
    }


    TextDocumentDomainObject.TemplateNames getTemplateNames(int docId) {
        TemplateNames jpaTemplateNames = templateNamesRepository.findOne(docId);

        if (jpaTemplateNames == null) return null;

        TextDocumentDomainObject.TemplateNames templateNamesDO = new TextDocumentDomainObject.TemplateNames();

        templateNamesDO.setDefaultTemplateName(jpaTemplateNames.getDefaultTemplateName());
        templateNamesDO.setDefaultTemplateNameForRestricted1(jpaTemplateNames.getDefaultTemplateNameForRestricted1());
        templateNamesDO.setDefaultTemplateNameForRestricted2(jpaTemplateNames.getDefaultTemplateNameForRestricted2());
        templateNamesDO.setTemplateGroupId(jpaTemplateNames.getTemplateGroupId());
        templateNamesDO.setTemplateName(jpaTemplateNames.getTemplateName());

        return templateNamesDO;
    }

    public Map<Integer, TextDomainObject> getTexts(final DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return textRepository.findByVersionAndLanguageWhereLoopEntryRefIsNull(version, language)
                .stream().collect(toMap(Text::getIndex, this::toDomainObject));
    }

    public Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> getLoopTexts(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        final Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    text.getLoopEntryRef().getLoopIndex(), text.getLoopEntryRef().getLoopEntryIndex(), text.getIndex()
            );

            result.put(loopItemRef, toDomainObject(text));
        }

        return result;
    }

    /**
     * Return text history based on special document {@link Version}, {@link Language}, and text id
     *
     * @param docRef {@link DocRef} item
     * @param textNo text id
     * @return {@link Set<TextHistory>} of text history
     * @see Version
     * @see Language
     * @see DocRef
     * @see imcode.server.document.DocumentDomainObject
     */
    public Collection<TextHistory> getTextHistory(DocRef docRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return textHistoryRepository.findAllByVersionAndLanguageAndNo(version, language, textNo);
    }

    /**
     * Return text history based on special document {@link Version}, {@link Language},{@link LoopEntryRef} and text id
     *
     * @param docRef {@link DocRef} item
     * @param textNo text id
     * @return {@link Collection<TextHistory>} of text history
     * @see Version
     * @see Language
     * @see DocRef
     * @see LoopEntryRef
     * @see imcode.server.document.DocumentDomainObject
     */
    public Collection<TextHistory> getTextHistory(DocRef docRef, LoopEntryRef loopEntryRef, int textNo) {
        if (loopEntryRef == null) {
            return getTextHistory(docRef, textNo);
        }

        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return textHistoryRepository.findAllByVersionAndLanguageAndLoopEntryRefAndNo(version, language, loopEntryRef, textNo);
    }


    public TextDomainObject getText(DocRef docRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return toDomainObject(
                textRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, textNo)
        );
    }

    public Map<Integer, ImageDomainObject> getImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNull(version, language)
                .stream().collect(toMap(Image::getIndex, ImcmsImageUtils::toDomainObject));

    }

    public Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> getLoopImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        final Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    image.getLoopEntryRef().getLoopIndex(), image.getLoopEntryRef().getLoopEntryIndex(), image.getIndex()
            );

            result.put(loopItemRef, ImcmsImageUtils.toDomainObject(image));
        }

        return result;
    }

    public Map<DocumentLanguage, ImageDomainObject> getImages(VersionRef versionRef, int imageNo, Optional<com.imcode.imcms.mapping.container.LoopEntryRef> loopEntryRefOpt) {
        return loopEntryRefOpt.isPresent()
                ? getLoopImages(versionRef, TextDocumentDomainObject.LoopItemRef.of(loopEntryRefOpt.get(), imageNo))
                : getImages(versionRef, imageNo);
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
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Image image : imageRepository.findByVersionAndIndexAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(languageMapper.toApiObject(image.getLanguage()), ImcmsImageUtils.toDomainObject(image));
        }

        return result;
    }

    public ImageDomainObject getImage(DocRef docRef, int imageNo, Optional<com.imcode.imcms.mapping.container.LoopEntryRef> loopEntryRefOpt) {
        return loopEntryRefOpt.isPresent()
                ? getLoopImage(docRef, TextDocumentDomainObject.LoopItemRef.of(loopEntryRefOpt.get(), imageNo))
                : getImage(docRef, imageNo);
    }

    public ImageDomainObject getImage(DocRef docRef, int imageNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return ImcmsImageUtils.toDomainObject(
                imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, imageNo)
        );
    }

    public ImageDomainObject getLoopImage(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return ImcmsImageUtils.toDomainObject(
                imageRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }


    public Map<Integer, Loop> getLoops(VersionRef versionRef) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());

        return loopRepository.findByVersion(version).stream().collect(toMap(loop -> loop.getIndex(), this::toApiObject));
    }


    public Map<Integer, MenuDomainObject> getMenus(VersionRef versionRef) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());

        return menuRepository.findByVersion(version).stream().collect(toMap(Menu::getNo, this::toDomainObject));
    }

    public Map<Integer, Integer> getIncludes(int docId) {
        return includeRepository.findByDocId(docId)
                .stream().collect(toMap(Include::getNo, Include::getIncludedDocumentId));
    }

    private TextDomainObject toDomainObject(Text jpaText) {
        return jpaText == null
                ? null
                : new TextDomainObject(jpaText.getText(), jpaText.getType().ordinal());
    }

    private MenuDomainObject toDomainObject(Menu menu) {
        MenuDomainObject menuDO = new MenuDomainObject();

        menuDO.setSortOrder(menu.getSortOrder());

        menu.getItems().forEach((referencedDocumentId, menuItem) -> {
            MenuItemDomainObject menuItemDO = new MenuItemDomainObject();
            GetterDocumentReference gtr = new GetterDocumentReference(referencedDocumentId, menuItemDocumentGetter);

            menuItemDO.setDocumentReference(gtr);
            menuItemDO.setSortKey(menuItem.getSortKey());
            menuItemDO.setTreeSortIndex(menuItem.getTreeSortIndex());
            menuItemDO.setId(menuItem.getId());

            menuDO.addMenuItemUnchecked(menuItemDO);
        });

        return menuDO;
    }

    private Loop toApiObject(com.imcode.imcms.persistence.entity.Loop jpaLoop) {
        if (jpaLoop == null) {
            return null;

        } else {
            Map<Integer, Boolean> entries = new ListOrderedMap<>();
            jpaLoop.getEntries().forEach(entry -> entries.put(entry.getIndex(), entry.isEnabled()));
            return new Loop(entries);
        }
    }
}
