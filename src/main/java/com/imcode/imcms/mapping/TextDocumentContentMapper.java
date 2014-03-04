package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.DocVersionRef;
import com.imcode.imcms.mapping.container.TextDocImageContainer;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class TextDocumentContentMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private DocVersionRepository versionRepository;

    @Inject
    private TextRepository textRepository;

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

    public void saveTemplateNames(int docId, TextDocumentDomainObject.TemplateNames templateNamesDO) {
        TemplateNames jpaTemplateNames = new TemplateNames();

        jpaTemplateNames.setDocId(docId);
        jpaTemplateNames.setDefaultTemplateName(templateNamesDO.getDefaultTemplateName());
        jpaTemplateNames.setDefaultTemplateNameForRestricted1(templateNamesDO.getDefaultTemplateNameForRestricted1());
        jpaTemplateNames.setDefaultTemplateNameForRestricted2(templateNamesDO.getDefaultTemplateNameForRestricted2());
        jpaTemplateNames.setTemplateGroupId(templateNamesDO.getTemplateGroupId());
        jpaTemplateNames.setTemplateName(templateNamesDO.getTemplateName());

        templateNamesRepository.save(jpaTemplateNames);
    }

    public Map<Integer, TextDomainObject> getTexts(final DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        final Map<Integer, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByDocVersionAndLanguageAndLoopEntryRefIsNull(version, language)) {
            result.put(text.getNo(), toDomainObject(text));
        }

        return result;
    }

    public Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> getLoopTexts(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        final Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByDocVersionAndLanguageAndLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    text.getLoopEntryRef().getLoopNo(), text.getLoopEntryRef().getContentNo(), text.getNo()
            );

            result.put(loopItemRef, toDomainObject(text));
        }

        return result;
    }


    public Map<DocumentLanguage, TextDomainObject> getTexts(DocVersionRef docVersionRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByDocVersionAndNoAndLoopEntryRefIsNull(version, textNo)) {
            result.put(languageMapper.toApiObject(text.getLanguage()), toDomainObject(text));
        }

        return result;
    }

    public Map<DocumentLanguage, TextDomainObject> getLoopTexts(DocVersionRef docVersionRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, TextDomainObject> result = new HashMap<>();
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Text text : textRepository.findByDocVersionAndNoAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(languageMapper.toApiObject(text.getLanguage()), toDomainObject(text));
        }

        return result;
    }

    public TextDomainObject getText(DocRef docRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        return toDomainObject(
                textRepository.findByDocVersionAndLanguageAndNoAndLoopEntryIsNull(version, language, textNo)
        );
    }

    public TextDomainObject getLoopText(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return toDomainObject(
                textRepository.findByDocVersionAndLanguageAndNoAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }

    public Map<Integer, ImageDomainObject> getImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        final Map<Integer, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByDocVersionAndLanguageAndLoopEntryRefIsNull(version, language)) {
            result.put(image.getNo(), toDomainObject(image));
        }

        return result;
    }

    public Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> getLoopImages(DocRef docRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        final Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByDocVersionAndLanguageAndLoopEntryRefIsNotNull(version, language)) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = TextDocumentDomainObject.LoopItemRef.of(
                    image.getLoopEntryRef().getLoopNo(), image.getLoopEntryRef().getContentNo(), image.getNo()
            );

            result.put(loopItemRef, toDomainObject(image));
        }

        return result;
    }

    public Map<DocumentLanguage, ImageDomainObject> getImages(DocVersionRef docVersionRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByDocVersionAndNoAndLoopEntryRefIsNull(version, textNo)) {
            result.put(languageMapper.toApiObject(image.getLanguage()), toDomainObject(image));
        }

        return result;
    }

    public Map<DocumentLanguage, ImageDomainObject> getLoopImages(DocVersionRef docVersionRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, ImageDomainObject> result = new HashMap<>();
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Image image : imageRepository.findByDocVersionAndNoAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(languageMapper.toApiObject(image.getLanguage()), toDomainObject(image));
        }

        return result;
    }


    public ImageDomainObject getImage(DocRef docRef, int textNo) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        return toDomainObject(
                imageRepository.findByDocVersionAndLanguageAndNoAndLoopEntryRefIsNull(version, language, textNo)
        );
    }

    public ImageDomainObject getLoopImage(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return toDomainObject(
                imageRepository.findByDocVersionAndLanguageAndNoAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }


    public void saveImage(TextDocImageContainer imageContainer) {
        Image image = toJpaObject(imageContainer);

        saveImage(image);
    }

    private void saveImage(Image image) {
        imageRepository.save(image);

        // fixme: save history
        // TextDocImageHistory textDocImageHistory = new TextDocImageHistory(image, user);
        // textDocRepository.saveImageHistory(textDocImageHistory);
    }

    public void saveContent(TextDocumentDomainObject doc) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        replaceImages(doc, version, language);
    }

    private void replaceImages(TextDocumentDomainObject doc, Version version, Language language) {
        DocRef docRef = doc.getRef();

        imageRepository.deleteByDocVersionAndLanguage(version, language);

        for (Map.Entry<Integer, ImageDomainObject> entry : doc.getImages().entrySet()) {
            Image image = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveImage(image);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> entry : doc.getLoopImages().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            Image image = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            saveImage(image);
        }
    }


    public void saveText(TextDocTextContainer textContainer) {
        Version version = versionRepository.findByDocIdAndNo(textContainer.getDocRef().getDocId(), textContainer.getDocRef().getDocVersionNo());
        Language language = languageRepository.findByCode(textContainer.getDocRef().getDocLanguageCode());

        TextDomainObject textDO = textContainer.getText();
        Text jpaText = new Text();

        jpaText.setLanguage(language);
        jpaText.setVersion(version);
        jpaText.setNo(textContainer.getTextNo());
        jpaText.setText(textDO.getText());
        jpaText.setType(TextType.values()[textDO.getType()]);
        com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef = textContainer.getLoopEntryRef();
        if (loopEntryRef != null) {
            jpaText.setLoopEntryRef(new LoopEntryRef(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo()));
        }

        textRepository.save(jpaText);

        // fixme: history
        //TextDocTextHistory textHistory = new TextDocTextHistory(textRef, user);
        //textRepository.saveTextHistory(textHistory);
    }


    public Map<Integer, Loop> getLoops(DocVersionRef docVersionRef) {
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());

        Map<Integer, Loop> result = new HashMap<>();

        for (com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop loop : loopRepository.findByDocVersion(version)) {
            result.put(
                    loop.getNo(),
                    toApiObject(loop)
            );
        }

        return result;
    }

    public Loop getLoop(DocVersionRef docVersionRef, int loopNo) {
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());

        return toApiObject(loopRepository.findByDocVersionAndNo(version, loopNo));
    }


    public void addLoopEntry(DocRef docRef, com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef) {
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop jpaLoop =
                loopRepository.findByDocVersionAndNo(version, loopEntryRef.getLoopNo());

        if (jpaLoop == null) {
            jpaLoop = new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop();
            jpaLoop.setNo(loopEntryRef.getLoopNo());
            jpaLoop.getEntries().add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(loopEntryRef.getEntryNo()));
            loopRepository.save(jpaLoop);
        } else {
            Loop apiLoop = toApiObject(jpaLoop);
            int contentNo = loopEntryRef.getEntryNo();
            if (!apiLoop.findEntryIndexByNo(contentNo).isPresent()) {
                jpaLoop.getEntries().add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(contentNo));
                loopRepository.save(jpaLoop);
            }
        }

    }


    public Map<Integer, MenuDomainObject> getMenus(DocVersionRef docVersionRef) {
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        List<Menu> textDocMenus = menuRepository.getByDocVersion(version);
        Map<Integer, MenuDomainObject> menus = new HashMap<>();

        for (Menu menu : textDocMenus) {
            menus.put(menu.getNo(), toDomainObject(menu));
        }

        return menus;
    }

    public Map<Integer, Integer> getIncludes(int docId) {
        Map<Integer, Integer> result = new HashMap<>();

        for (Include include : includeRepository.findByDocId(docId)) {
            result.put(include.getNo(), include.getIncludedDocumentId());
        }

        return result;
    }

    private TextDomainObject toDomainObject(Text jpaText) {
        return jpaText == null
                ? null
                : new TextDomainObject(jpaText.getText(), jpaText.getType().ordinal());
    }

    private MenuDomainObject toDomainObject(Menu menu) {
        MenuDomainObject menuDO = new MenuDomainObject();

        menuDO.setSortOrder(menu.getSortOrder());

        for (Map.Entry<Integer, MenuItem> e : menu.getItems().entrySet()) {
            MenuItem menuItem = e.getValue();
            Integer referencedDocumentId = e.getKey();
            MenuItemDomainObject menuItemDO = new MenuItemDomainObject();
            GetterDocumentReference gtr = new GetterDocumentReference(referencedDocumentId, menuItemDocumentGetter);

            menuItemDO.setDocumentReference(gtr);
            menuItemDO.setSortKey(menuItem.getSortKey());
            menuItemDO.setTreeSortIndex(menuItem.getTreeSortIndex());

            menuDO.addMenuItemUnchecked(menuItemDO);
        }

        return menuDO;
    }



    public ImageDomainObject toDomainObject(Image image) {
        if (image == null) return null;

        ImageDomainObject imageDO = new ImageDomainObject();

        imageDO.setAlign(image.getAlign());
        imageDO.setAlternateText(image.getAlternateText());
        //fixme: check
        //imageDO.setArchiveImageId();
        imageDO.setBorder(image.getBorder());
        //imageDO.setCropRegion();
        imageDO.setGeneratedFilename(image.getGeneratedFilename());
        imageDO.setHeight(image.getHeight());
        imageDO.setHorizontalSpace(image.getHorizontalSpace());
        imageDO.setLinkUrl(image.getLinkUrl());
        imageDO.setLowResolutionUrl(image.getLowResolutionUrl());
        imageDO.setName(image.getName());
        //imageDO.setResize();
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

        Map<Integer, Boolean> entries = new HashMap<>();

        for (com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry jpaEntry : jpaLoop.getEntries()) {
            entries.put(jpaEntry.getNo(), jpaEntry.isEnabled());
        }

        return Loop.of(entries, jpaLoop.getNextEntryNo());
    }



    private Image toJpaObject(TextDocImageContainer imageContainer) {
        Language language = languageRepository.findByCode(imageContainer.getDocLanguageCode());
        Version version = versionRepository.findByDocIdAndNo(imageContainer.getDocId(), imageContainer.getDocVersionNo());

        com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRefDO = imageContainer.getLoopEntryRef();
        LoopEntryRef loopEntryRef = loopEntryRefDO == null
                ? null
                : new LoopEntryRef(loopEntryRefDO.getLoopNo(), loopEntryRefDO.getEntryNo());

        return toJpaObject(imageContainer.getImage(), version, language, imageContainer.getImageNo(), loopEntryRef);
    }

    private Image toJpaObject(ImageDomainObject imageDO, Version version, Language language, int no, LoopEntryRef loopEntryRef) {
        ImageDomainObject.CropRegion cropRegionDO = imageDO.getCropRegion();
        ImageCropRegion cropRegion = cropRegionDO.isValid()
                ? new ImageCropRegion(cropRegionDO.getCropX1(), cropRegionDO.getCropY1(), cropRegionDO.getCropX2(), cropRegionDO.getCropY2())
                : new ImageCropRegion(-1, -1, -1, -1);


        Image image = new Image();

        image.setNo(no);
        image.setLanguage(language);
        image.setVersion(version);
        image.setLoopEntryRef(loopEntryRef);
        image.setAlign(imageDO.getAlign());
        image.setAlternateText(imageDO.getAlternateText());
        image.setBorder(imageDO.getBorder());
        image.setCropRegion(cropRegion);
        image.setFormat(imageDO.getFormat() == null ? 0 : imageDO.getFormat().getOrdinal());
        image.setGeneratedFilename(imageDO.getGeneratedFilename());
        image.setHeight(imageDO.getHeight());
        image.setHorizontalSpace(imageDO.getHorizontalSpace());
        image.setUrl(imageDO.getSource().toStorageString());
        image.setLinkUrl(imageDO.getLinkUrl());
        image.setLowResolutionUrl(imageDO.getLowResolutionUrl());
        image.setName(imageDO.getName());
        image.setResize(imageDO.getResize() == null ? 0 : imageDO.getResize().getOrdinal());
        image.setRotateAngle(imageDO.getRotateDirection() == null ? 0 : imageDO.getRotateDirection().getAngle());
        image.setTarget(imageDO.getTarget());
        image.setType(imageDO.getSource().getTypeId());
        image.setVerticalSpace(imageDO.getVerticalSpace());
        image.setWidth(imageDO.getWidth());
        image.setHeight(imageDO.getHeight());

        return image;
    }

}
