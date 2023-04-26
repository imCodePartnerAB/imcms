package com.imcode.imcms.mapping;

import com.imcode.imcms.domain.dto.ImageCropRegionDTO;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.enums.SaveMode;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.*;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.image.Format;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TextDocumentContentSaver {

    private final Logger logger = LogManager.getLogger(TextDocumentContentSaver.class);
    private final MenuService menuService;
    private final VersionRepository versionRepository;
    private final TextRepository textRepository;
    private final ImageRepository imageRepository;
    private final TextDocumentTemplateRepository textDocumentTemplateRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final TextService textService;
    private final LoopService loopService;

    public TextDocumentContentSaver(MenuService menuService,
                                    VersionRepository versionRepository,
                                    TextRepository textRepository,
                                    ImageRepository imageRepository,
                                    TextDocumentTemplateRepository textDocumentTemplateRepository,
                                    LanguageRepository languageRepository,
                                    UserRepository userRepository,
                                    TextService textService,
                                    LoopService loopService) {

        this.menuService = menuService;
        this.versionRepository = versionRepository;
        this.textRepository = textRepository;
        this.imageRepository = imageRepository;
        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
        this.languageRepository = languageRepository;
        this.userRepository = userRepository;
        this.textService = textService;
        this.loopService = loopService;
    }

    /**
     * Saves new document content.
     */
    public void createContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        LanguageJPA language = findLanguage(docRef);

        // loops must be created before loop items (texts and images)
//        createLoops(doc, version);
        saveTexts(doc, version, language, SaveMode.CREATE);
        saveImages(doc, version, language, SaveMode.CREATE);
//        saveMenus(doc, version, SaveMode.CREATE);

        saveTemplateNames(doc.getId(), doc.getTemplateNames());
    }

    public void createCommonContent(TextDocumentDomainObject doc) {
        VersionRef versionRef = doc.getVersionRef();
        Version version = findVersion(versionRef);

//        createLoops(doc, version);
//        saveMenus(doc, version, SaveMode.CREATE);
        saveTemplateNames(doc.getId(), doc.getTemplateNames());
    }

    public void createI18nContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        LanguageJPA language = findLanguage(docRef);

        saveTexts(doc, version, language, SaveMode.CREATE);
        saveImages(doc, version, language, SaveMode.CREATE);
    }

    /**
     * Updates existing document content.
     */
    public void updateContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        LanguageJPA language = findLanguage(docRef);

        // loop items must be deleted before loops (texts and images)
        textRepository.deleteByVersionAndLanguage(version, language);
        imageRepository.deleteByVersionAndLanguage(version, language);
        menuService.deleteByVersion(version);
        // loops must be re-created before loop items (texts and images)
       /* loopRepository.findByVersion(version).forEach((a) -> a.getEntries().clear());
        loopRepository.deleteByVersion(version);

        createLoops(doc, version);*/
        saveTexts(doc, version, language, SaveMode.UPDATE);
        saveImages(doc, version, language, SaveMode.UPDATE);
//        saveMenus(doc, version, SaveMode.UPDATE);

        saveTemplateNames(doc.getId(), doc.getTemplateNames());
    }

    /**
     * Saves existing document image.
     */
    public void saveImage(TextDocImageContainer container) {
        ImageJPA image = toJpaObject(container);

        saveImage(image, SaveMode.UPDATE);
    }

    public void saveImages(TextDocImagesContainer container) {
        Version version = findVersion(container);

        for (Map.Entry<Language, ImageDomainObject> e : container.getImages().entrySet()) {
            ImageJPA image = toJpaObject(e.getValue(), version, new LanguageJPA(e.getKey()), container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, SaveMode.UPDATE);
        }

        container.getImages().forEach((language, imageDO) -> {
            ImageJPA image = toJpaObject(imageDO, version, new LanguageJPA(language), container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, SaveMode.UPDATE);
        });
    }

    public void saveText(TextDocTextContainer container, UserDomainObject userDomainObject) {
        TextJPA text = toJpaObject(container);

        textService.save(text);
    }

    public void saveTexts(TextDocTextsContainer container, UserDomainObject userDomainObject) {
        Version version = findVersion(container);

        container.getTexts().forEach((language, textDO) -> {
            TextJPA text = toJpaObject(textDO, version, new LanguageJPA(language), container.getTextNo(), toJpaObject(container.getLoopEntryRef()));

            textService.save(text);
        });
    }

    private void saveTemplateNames(int docId, TextDocumentDomainObject.TemplateNames templateNamesDO) {
        TextDocumentTemplateJPA textDocumentTemplate = new TextDocumentTemplateJPA();

        textDocumentTemplate.setDocId(docId);
        textDocumentTemplate.setChildrenTemplateName(templateNamesDO.getDefaultTemplateName());
        textDocumentTemplate.setTemplateName(templateNamesDO.getTemplateName());

        textDocumentTemplateRepository.save(textDocumentTemplate);
    }

    public void saveMenu(MenuContainer container) {
        menuService.saveFrom(container.getMenu());
    }

    private void saveImages(TextDocumentDomainObject doc, Version version, LanguageJPA language, SaveMode saveMode) {
        for (Map.Entry<Integer, ImageDomainObject> entry : doc.getImages().entrySet()) {
            ImageJPA image = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveImage(image, saveMode);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> entry : doc.getLoopImages().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            ImageJPA image = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            saveImage(image, saveMode);
        }
    }

    private void saveTexts(TextDocumentDomainObject doc, Version version, LanguageJPA language, SaveMode saveMode) {
        for (Map.Entry<Integer, TextDomainObject> entry : doc.getTexts().entrySet()) {
            TextJPA text = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            textService.save(text);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, TextDomainObject> entry : doc.getLoopTexts().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            TextJPA text = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            textService.save(text);
        }
    }

    private void saveImage(ImageJPA image, SaveMode saveMode) {
        if (saveMode == SaveMode.UPDATE) {
            LoopEntryRefJPA loopEntryRef = image.getLoopEntryRef();
            Integer id = loopEntryRef == null
                    ? imageRepository.findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(image.getVersion(), image.getLanguage(), image.getIndex())
                    : imageRepository.findIdByVersionAndLanguageAndIndexAndLoopEntryRef(image.getVersion(), image.getLanguage(), image.getIndex(), loopEntryRef);

            image.setId(id);
        }

        loopService.createLoopEntryIfNotExists(image.getVersion(), image.getLoopEntryRef());
        imageRepository.save(image);
    }

    private TextJPA toJpaObject(TextDocTextContainer container) {
        LanguageJPA language = findLanguage(container);
        Version version = findVersion(container);
        LoopEntryRefJPA loopEntryRef = toJpaObject(container.getLoopEntryRef());

        return toJpaObject(container.getText(), version, language, container.getTextNo(), loopEntryRef);
    }

    private TextJPA toJpaObject(TextDomainObject textDO, Version version, LanguageJPA language, int no, LoopEntryRefJPA loopEntryRef) {
        TextJPA text = new TextJPA();

        text.setLanguage(language);
        text.setVersion(version);
        text.setIndex(no);
        text.setText(textDO.getText());
        text.setType(Text.Type.values()[textDO.getType()]);
        text.setLoopEntryRef(loopEntryRef);
        text.setLikePublished(textDO.isLikePublished());

        return text;
    }

    private ImageJPA toJpaObject(TextDocImageContainer container) {
        LanguageJPA language = findLanguage(container);
        Version version = findVersion(container);
        LoopEntryRefJPA loopEntryRef = toJpaObject(container.getLoopEntryRef());

        return toJpaObject(container.getImage(), version, language, container.getImageNo(), loopEntryRef);
    }

    private ImageJPA toJpaObject(ImageDomainObject imageDO, Version version, LanguageJPA language, int no, LoopEntryRefJPA loopEntryRef) {
        ImageCropRegionDTO cropRegionDO = imageDO.getCropRegion();
        ImageCropRegionJPA cropRegion = cropRegionDO.isValid()
                ? new ImageCropRegionJPA(cropRegionDO)
                : new ImageCropRegionJPA(-1, -1, -1, -1);

        ImageJPA image = new ImageJPA();

        image.setIndex(no);
        image.setLanguage(language);
        image.setVersion(version);
        image.setLoopEntryRef(loopEntryRef);
        image.setAlign(imageDO.getAlign());
        image.setAlternateText(imageDO.getAlternateText());
        image.setBorder(imageDO.getBorder());
        image.setCropRegion(cropRegion);
        image.setFormat(Optional.ofNullable(imageDO.getFormat()).orElse(Format.BMP)); // change if need
        image.setGeneratedFilename(imageDO.getGeneratedFilename());
        image.setHeight(imageDO.getHeight());
        image.setSpaceAround(imageDO.getSpaceAround());
        image.setUrl(imageDO.getSource().toStorageString());
        image.setLinkUrl(imageDO.getLinkUrl());
        image.setLowResolutionUrl(imageDO.getLowResolutionUrl());
        image.setName(imageDO.getName());
        image.setResize(imageDO.getResize() == null ? 0 : imageDO.getResize().getOrdinal());
        image.setRotateAngle(imageDO.getRotateDirection() == null ? 0 : imageDO.getRotateDirection().getAngle());
        image.setTarget(imageDO.getTarget());
        image.setType(imageDO.getSource().getTypeId());
        image.setWidth(imageDO.getWidth());
        image.setHeight(imageDO.getHeight());
        image.setArchiveImageId(imageDO.getArchiveImageId());

        return image;
    }

    private LoopEntryRefJPA toJpaObject(com.imcode.imcms.mapping.container.LoopEntryRef source) {
        return source == null
                ? null
                : new LoopEntryRefJPA(source.getLoopNo(), source.getEntryNo());
    }

    private LoopJPA toJpaObject(TextDocLoopContainer container) {
        return toJpaObject(container.getVersionRef(), container.getLoop());
    }

    private LoopJPA toJpaObject(VersionRef versionRef, Loop loop) {
        Version version = findVersion(versionRef);

        return new LoopJPA(loop, version);
    }

    private Version findVersion(DocRef docRef) {
        return versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
    }

    private Version findVersion(VersionRef versionRef) {
        return versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());
    }

    private Version findVersion(Container container) {
        return versionRepository.findByDocIdAndNo(container.getDocId(), container.getVersionNo());
    }

    private User findUser(UserDomainObject userDomainObject) {
        return userRepository.getOne(userDomainObject.getId());
    }

    private LanguageJPA findLanguage(LanguageContainer container) {
        return languageRepository.findByCode(container.getLanguageCode());
    }

    private LanguageJPA findLanguage(com.imcode.imcms.api.DocumentLanguage documentLanguage) {
        return languageRepository.findByCode(documentLanguage.getCode());
    }
}
