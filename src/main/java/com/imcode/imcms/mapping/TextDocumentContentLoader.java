package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;
import imcode.util.image.Resize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class TextDocumentContentLoader {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private VersionRepository versionRepository;

    @Inject
    private TextRepository textRepository;

    @Inject
    private TextHistoryRepository textHistoryRepository;

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private MenuRepository menuRepository;

    @Inject
    private TemplateNamesRepository templateNamesRepository;

    @Inject
    private LoopRepository loopRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private IncludeRepository includeRepository;

    @Inject
    private DocumentGetter menuItemDocumentGetter;

    @Inject
    private DocumentLanguageMapper languageMapper;

    public TextDocumentDomainObject.TemplateNames getTemplateNames(int docId) {
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
                .stream().collect(toMap(Text::getNo, this::toDomainObject));
    }

    public Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> getLoopTexts(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        final Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    text.getLoopEntryRef().getLoopNo(), text.getLoopEntryRef().getEntryNo(), text.getNo()
            );

            result.put(loopItemRef, toDomainObject(text));
        }

        return result;
    }


    public Map<DocumentLanguage, TextDomainObject> getTexts(VersionRef versionRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());
        Map<DocumentLanguage, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByVersionAndNoWhereLoopEntryRefIsNull(version, textNo)) {
            result.put(languageMapper.toApiObject(text.getLanguage()), toDomainObject(text));
        }

        return result;
    }

    public Map<DocumentLanguage, TextDomainObject> getLoopTexts(VersionRef versionRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());
        Map<DocumentLanguage, TextDomainObject> result = new HashMap<>();
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Text text : textRepository.findByVersionAndNoAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(languageMapper.toApiObject(text.getLanguage()), toDomainObject(text));
        }

        return result;
    }

    public TextDomainObject getFirstLoopEntryText(DocRef docRef, com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        LoopEntryRef loopEntryRefJpa = new LoopEntryRef(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo());

        return toDomainObject(
                textRepository.findFirst(version, language, loopEntryRefJpa)
        );
    }

    public Set<TextHistory> getTextHistory(int docId, int textNo) {
        return textHistoryRepository.findAllByDocumentAndTextNo(docId, textNo);
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
     *
     * @see Version
     * @see Language
     * @see DocRef
     * @see LoopEntryRef
     * @see imcode.server.document.DocumentDomainObject
     */
    public Collection<TextHistory> getTextHistory(DocRef docRef, LoopEntryRef loopEntryRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return textHistoryRepository.findAllByVersionAndLanguageAndLoopEntryRefAndNo(version, language, loopEntryRef, textNo);
    }


    public TextDomainObject getText(DocRef docRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return toDomainObject(
                textRepository.findByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(version, language, textNo)
        );
    }

    public TextDomainObject getLoopText(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return toDomainObject(
                textRepository.findByVersionAndLanguageAndNoAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }

    public Map<Integer, ImageDomainObject> getImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());

        return imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNull(version, language)
                .stream().collect(toMap(Image::getNo, this::toDomainObject));

    }

    public Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> getLoopImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        final Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    image.getLoopEntryRef().getLoopNo(), image.getLoopEntryRef().getEntryNo(), image.getNo()
            );

            result.put(loopItemRef, toDomainObject(image));
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

        for (Image image : imageRepository.findByVersionAndNoWhereLoopEntryRefIsNull(version, imageNo)) {
            result.put(languageMapper.toApiObject(image.getLanguage()), toDomainObject(image));
        }

        return result;
    }

    public Map<DocumentLanguage, ImageDomainObject> getLoopImages(VersionRef versionRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());
        Map<DocumentLanguage, ImageDomainObject> result = new HashMap<>();
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Image image : imageRepository.findByVersionAndNoAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(languageMapper.toApiObject(image.getLanguage()), toDomainObject(image));
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

        return toDomainObject(
                imageRepository.findByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(version, language, imageNo)
        );
    }

    public ImageDomainObject getLoopImage(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return toDomainObject(
                imageRepository.findByVersionAndLanguageAndNoAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }


    public Map<Integer, Loop> getLoops(VersionRef versionRef) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());

        return loopRepository.findByVersion(version).stream().collect(toMap(loop -> loop.getNo(), this::toApiObject));
    }

    public Loop getLoop(VersionRef versionRef, int loopNo) {
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());

        return toApiObject(loopRepository.findByVersionAndNo(version, loopNo));
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

            menuDO.addMenuItemUnchecked(menuItemDO);
        });

        return menuDO;
    }


    private ImageDomainObject toDomainObject(Image image) {
        if (image == null) return null;

        ImageDomainObject imageDO = new ImageDomainObject();

        imageDO.setAlign(image.getAlign());
        imageDO.setAlternateText(image.getAlternateText());
        imageDO.setArchiveImageId(image.getArchiveImageId());
        imageDO.setBorder(image.getBorder());

        ImageCropRegion cropRegion = image.getCropRegion();
        ImageDomainObject.CropRegion cropRegionDO = new ImageDomainObject.CropRegion(
                cropRegion.getCropX1(), cropRegion.getCropY1(), cropRegion.getCropX2(), cropRegion.getCropY2()
        );
        imageDO.setCropRegion(cropRegionDO);
        imageDO.setGeneratedFilename(image.getGeneratedFilename());
        imageDO.setHeight(image.getHeight());
        imageDO.setHorizontalSpace(image.getHorizontalSpace());
        imageDO.setLinkUrl(image.getLinkUrl());
        imageDO.setLowResolutionUrl(image.getLowResolutionUrl());
        imageDO.setName(image.getName());
        imageDO.setResize(Resize.getByOrdinal(image.getResize()));
        imageDO.setTarget(image.getTarget());
        imageDO.setVerticalSpace(image.getVerticalSpace());
        imageDO.setWidth(image.getWidth());

        return initImageSource(image, imageDO);
    }


    private ImageDomainObject initImageSource(Image jpaImage, ImageDomainObject imageDO) {
        String url = jpaImage.getUrl();
        Integer type = jpaImage.getType();

        Objects.requireNonNull(url);
        Objects.requireNonNull(type);

        imageDO.setSource(createImageSource(imageDO, url.trim(), type));

        return imageDO;
    }

    private ImageSource createImageSource(ImageDomainObject image, String url, int type) {
        switch (type) {
            case ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT:
                throw new IllegalStateException(
                        String.format("Illegal image source type - IMAGE_TYPE_ID__FILE_DOCUMENT. Image: %s", image)
                );

            case ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH:
                return new ImagesPathRelativePathImageSource(url);

            case ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE:
                return new ImageArchiveImageSource(url);

            default:
                return new NullImageSource();
        }
    }


    private Loop toApiObject(com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop jpaLoop) {
        if (jpaLoop == null) return null;

        Map<Integer, Boolean> entries = jpaLoop.getEntries()
                .stream().collect(toMap(entry -> entry.getNo(), entry -> entry.isEnabled()));

        return Loop.of(entries, jpaLoop.getNextEntryNo());
    }
}
