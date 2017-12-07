package com.imcode.imcms.mapping;

import com.imcode.imcms.domain.dto.ImageData.ImageCropRegionDTO;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Menu;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.MenuItem;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.MenuRepository;
import com.imcode.imcms.model.LoopEntry;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.*;
import com.imcode.imcms.util.Value;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TextDocumentContentSaver {

    private final VersionRepository versionRepository;
    private final TextRepository textRepository;
    private final TextHistoryRepository textHistoryRepository;
    private final ImageRepository imageRepository;
    private final MenuRepository menuRepository;
    private final TextDocumentTemplateRepository textDocumentTemplateRepository;
    private final LoopRepository loopRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;

    @Inject
    public TextDocumentContentSaver(VersionRepository versionRepository, TextRepository textRepository,
                                    TextHistoryRepository textHistoryRepository, ImageRepository imageRepository,
                                    MenuRepository menuRepository, TextDocumentTemplateRepository textDocumentTemplateRepository,
                                    LoopRepository loopRepository, LanguageRepository languageRepository,
                                    UserRepository userRepository) {
        this.versionRepository = versionRepository;
        this.textRepository = textRepository;
        this.textHistoryRepository = textHistoryRepository;
        this.imageRepository = imageRepository;
        this.menuRepository = menuRepository;
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
        menuRepository.deleteByVersion(version);
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
        Image image = toJpaObject(container);

        saveImage(image, SaveMode.UPDATE);
    }

    public void saveImages(TextDocImagesContainer container) {
        Version version = findVersion(container);

        for (Map.Entry<com.imcode.imcms.api.DocumentLanguage, ImageDomainObject> e : container.getImages().entrySet()) {
            LanguageJPA language = findLanguage(e.getKey());
            Image image = toJpaObject(e.getValue(), version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, SaveMode.UPDATE);
        }

        container.getImages().forEach((languageDO, imageDO) -> {
            LanguageJPA language = findLanguage(languageDO);
            Image image = toJpaObject(imageDO, version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

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

    public void saveMenu(TextDocMenuContainer container) {
        VersionRef versionRef = container.getVersionRef();
        Version version = findVersion(versionRef);
        Menu menu = toJpaObject(container.getMenu(), version, container.getMenuNo());

        saveMenu(menu, SaveMode.UPDATE);

    }

    private Menu toJpaObject(MenuDomainObject menuDO, Version version, int no) {
        Menu menu = new Menu();
        Map<Integer, MenuItem> menuItems = new HashMap<>();

        menuDO.getItemsMap().forEach((menuItemNo, menuItemDO) -> {
            MenuItem menuItem = new MenuItem();
            menuItem.setSortKey(menuItemDO.getSortKey());
            menuItem.setTreeSortIndex(menuItemDO.getTreeSortIndex());
            menuItems.put(menuItemNo, menuItem);
        });

        menu.setVersion(version);
        menu.setNo(no);
        menu.setSortOrder(menuDO.getSortOrder());
        menu.setItems(menuItems);

        return menu;
    }

    private void saveMenu(Menu menu, SaveMode saveMode) {
        if (saveMode == SaveMode.UPDATE) {
            Integer id = menuRepository.findIdByVersionAndNo(menu.getVersion(), menu.getNo());
            menu.setId(id);
        }

        menuRepository.saveAndFlush(menu);
    }

    private void saveImages(TextDocumentDomainObject doc, Version version, LanguageJPA language, SaveMode saveMode) {
        for (Map.Entry<Integer, ImageDomainObject> entry : doc.getImages().entrySet()) {
            Image image = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveImage(image, saveMode);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> entry : doc.getLoopImages().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            Image image = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

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

    private void saveImage(Image image, SaveMode saveMode) {
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
        if (saveMode == SaveMode.UPDATE) {
            LoopEntryRefJPA loopEntryRef = text.getLoopEntryRef();
            Integer id = loopEntryRef == null
                    ? textRepository.findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(text.getVersion(), text.getLanguage(), text.getIndex())
                    : textRepository.findIdByVersionAndLanguageAndIndexAndLoopEntryRef(text.getVersion(), text.getLanguage(), text.getIndex(), loopEntryRef);

            text.setId(id);
        }

        createLoopEntryIfNotExists(text.getVersion(), text.getLoopEntryRef());

        textRepository.save(text);
        textHistoryRepository.save(new TextHistory(text, user));
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

    private Image toJpaObject(TextDocImageContainer container) {
        LanguageJPA language = findLanguage(container);
        Version version = findVersion(container);
        LoopEntryRefJPA loopEntryRef = toJpaObject(container.getLoopEntryRef());

        return toJpaObject(container.getImage(), version, language, container.getImageNo(), loopEntryRef);
    }

    private Image toJpaObject(ImageDomainObject imageDO, Version version, LanguageJPA language, int no, LoopEntryRefJPA loopEntryRef) {
        ImageCropRegionDTO cropRegionDO = imageDO.getCropRegion();
        ImageCropRegionJPA cropRegion = cropRegionDO.isValid()
                ? new ImageCropRegionJPA(cropRegionDO)
                : new ImageCropRegionJPA(-1, -1, -1, -1);

        Image image = new Image();

        image.setIndex(no);
        image.setLanguage(language);
        image.setVersion(version);
        image.setLoopEntryRef(loopEntryRef);
        image.setAlign(imageDO.getAlign());
        image.setAlternateText(imageDO.getAlternateText());
        image.setBorder(imageDO.getBorder());
        image.setCropRegion(cropRegion);
        image.setFormat(imageDO.getFormat());
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
        image.setArchiveImageId(imageDO.getArchiveImageId());

        return image;
    }

    private LoopEntryRefJPA toJpaObject(com.imcode.imcms.mapping.container.LoopEntryRef source) {
        return source == null
                ? null
                : new LoopEntryRefJPA(source.getLoopNo(), source.getEntryNo());
    }

    private LoopJPA toJpaObject(TextDocLoopContainer container) {
        return toJpaObject(container.getVersionRef(), container.getLoopNo(), container.getLoop());
    }

    private LoopJPA toJpaObject(VersionRef versionRef, int loopNo, com.imcode.imcms.api.Loop loopDO) {
        List<LoopEntry> entries = new LinkedList<>();
        Version version = findVersion(versionRef);

        loopDO.getEntries().forEach((entryNo, enabled) -> entries.add(new LoopEntryJPA(entryNo, enabled)));

        return Value.with(
                new LoopJPA(),
                l -> {
                    l.setEntries(entries);
                    l.setIndex(loopNo);
                    l.setVersion(version);
                }
        );
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
