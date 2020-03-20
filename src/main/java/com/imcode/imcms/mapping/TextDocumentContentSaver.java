package com.imcode.imcms.mapping;

import com.imcode.imcms.domain.dto.ImageCropRegionDTO;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.mapping.container.Container;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.LanguageContainer;
import com.imcode.imcms.mapping.container.MenuContainer;
import com.imcode.imcms.mapping.container.TextDocImageContainer;
import com.imcode.imcms.mapping.container.TextDocImagesContainer;
import com.imcode.imcms.mapping.container.TextDocLoopContainer;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.container.TextDocTextsContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.ImageCropRegionJPA;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.LoopRepository;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import com.imcode.imcms.persistence.repository.TextRepository;
import com.imcode.imcms.persistence.repository.UserRepository;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.image.Format;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TextDocumentContentSaver {

    private final Logger logger = Logger.getLogger(TextDocumentContentSaver.class);
    private final MenuService menuService;
    private final VersionRepository versionRepository;
    private final TextRepository textRepository;
    private final TextHistoryRepository textHistoryRepository;
    private final ImageRepository imageRepository;
    private final TextDocumentTemplateRepository textDocumentTemplateRepository;
    private final LoopRepository loopRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;

    public TextDocumentContentSaver(MenuService menuService,
                                    VersionRepository versionRepository,
                                    TextRepository textRepository,
                                    TextHistoryRepository textHistoryRepository,
                                    ImageRepository imageRepository,
                                    TextDocumentTemplateRepository textDocumentTemplateRepository,
                                    LoopRepository loopRepository,
                                    LanguageRepository languageRepository,
                                    UserRepository userRepository) {

        this.menuService = menuService;
        this.versionRepository = versionRepository;
        this.textRepository = textRepository;
        this.textHistoryRepository = textHistoryRepository;
        this.imageRepository = imageRepository;
        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
        this.loopRepository = loopRepository;
        this.languageRepository = languageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Saves new document content.
     */
    public void createContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        LanguageJPA language = findLanguage(docRef);
        User user = findUser(userDomainObject);

        // loops must be created before loop items (texts and images)
//        createLoops(doc, version);
        saveTexts(doc, version, language, user, SaveMode.CREATE);
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
        User user = findUser(userDomainObject);

        saveTexts(doc, version, language, user, SaveMode.CREATE);
        saveImages(doc, version, language, SaveMode.CREATE);
    }

    /**
     * Updates existing document content.
     */
    public void updateContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        LanguageJPA language = findLanguage(docRef);
        User user = findUser(userDomainObject);

        // loop items must be deleted before loops (texts and images)
        textRepository.deleteByVersionAndLanguage(version, language);
        imageRepository.deleteByVersionAndLanguage(version, language);
        menuService.deleteByVersion(version);
        // loops must be re-created before loop items (texts and images)
       /* loopRepository.findByVersion(version).forEach((a) -> a.getEntries().clear());
        loopRepository.deleteByVersion(version);

        createLoops(doc, version);*/
        saveTexts(doc, version, language, user, SaveMode.UPDATE);
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

        for (Map.Entry<com.imcode.imcms.api.DocumentLanguage, ImageDomainObject> e : container.getImages().entrySet()) {
            LanguageJPA language = findLanguage(e.getKey());
            ImageJPA image = toJpaObject(e.getValue(), version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, SaveMode.UPDATE);
        }

        container.getImages().forEach((languageDO, imageDO) -> {
            LanguageJPA language = findLanguage(languageDO);
            ImageJPA image = toJpaObject(imageDO, version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, SaveMode.UPDATE);
        });
    }

    public void saveText(TextDocTextContainer container, UserDomainObject userDomainObject) {
        User user = findUser(userDomainObject);
        TextJPA text = toJpaObject(container);

        saveText(text, user, SaveMode.UPDATE);
    }

    public void saveTexts(TextDocTextsContainer container, UserDomainObject userDomainObject) {
        User user = findUser(userDomainObject);
        Version version = findVersion(container);

        container.getTexts().forEach((languageDO, textDO) -> {
            LanguageJPA language = findLanguage(languageDO);
            TextJPA text = toJpaObject(textDO, version, language, container.getTextNo(), toJpaObject(container.getLoopEntryRef()));

            saveText(text, user, SaveMode.UPDATE);
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

    private void saveTexts(TextDocumentDomainObject doc, Version version, LanguageJPA language, User user, SaveMode saveMode) {
        for (Map.Entry<Integer, TextDomainObject> entry : doc.getTexts().entrySet()) {
            TextJPA text = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveText(text, user, saveMode);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, TextDomainObject> entry : doc.getLoopTexts().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            TextJPA text = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            saveText(text, user, saveMode);
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

        createLoopEntryIfNotExists(image.getVersion(), image.getLoopEntryRef());
        imageRepository.save(image);
    }

    private void saveText(TextJPA text, User user, SaveMode saveMode) {
        logger.error(String.format("TextJpa exists %s", String.valueOf(text != null)));
        final Version version = text.getVersion();
        final LanguageJPA language = text.getLanguage();
        final Integer index = text.getIndex();

        if (saveMode == SaveMode.UPDATE) {

            final LoopEntryRefJPA loopEntryRef = text.getLoopEntryRef();
            logger.error(String.format("In TextJpa exists LoopEntry %s", String.valueOf(loopEntryRef != null)));
            final Integer id = (loopEntryRef == null)
                    ? textRepository.findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index)
                    : textRepository.findIdByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, index, loopEntryRef);

            text.setId(id);
        }

        createLoopEntryIfNotExists(version, text.getLoopEntryRef());
        logger.error(String.format("Prepare to save text with index %d and content %s and id %d", text.getIndex(), text.getText(), text.getId()));
        textRepository.save(text);
        logger.error(String.format("Text with index %d was saved with content %s and id %d", text.getIndex(), text.getText(), text.getId()));
        textHistoryRepository.save(new TextHistoryJPA(text, language, user));
    }

    private void createLoopEntryIfNotExists(Version version, LoopEntryRefJPA entryRef) {
        if (entryRef == null) return;

        LoopJPA loop = loopRepository.findByVersionAndIndex(
                version, entryRef.getLoopIndex());
        int entryIndex = entryRef.getLoopEntryIndex();
        int loopIndex = entryRef.getLoopIndex();

        if (loop == null) {
            loop = new LoopJPA();
            loop.setVersion(version);
            loop.setIndex(loopIndex);
            loop.getEntries().add(new LoopEntryJPA(entryIndex, true));
        } else {
            if (!loop.containsEntry(entryRef.getLoopEntryIndex())) {
                loop.getEntries().add(new LoopEntryJPA(entryIndex, true));
            }
        }
        loopRepository.save(loop);
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

    private enum SaveMode {
        CREATE, UPDATE
    }
}
