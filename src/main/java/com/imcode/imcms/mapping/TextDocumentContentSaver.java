package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopEntryRef;
import com.imcode.imcms.util.Value;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TextDocumentContentSaver {

    @Inject
    private VersionRepository versionRepository;
    @Inject
    private TextRepository textRepository;
    @Inject
    private TextHistoryRepository textHistoryRepository;
    @Inject
    private ImageRepository imageRepository;
    @Inject
    private ImageHistoryRepository imageHistoryRepository;
    @Inject
    private MenuRepository menuRepository;
    @Inject
    private MenuHistoryRepository menuHistoryRepository;
    @Inject
    private TemplateNamesRepository templateNamesRepository;
    @Inject
    private LoopRepository loopRepository;
    @Inject
    private LanguageRepository languageRepository;
    @Inject
    private IncludeRepository includeRepository;
    @Inject
    private UserRepository userRepository;

    /**
     * Saves new document content.
     */
    public void createContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        Language language = findLanguage(docRef);
        User user = findUser(userDomainObject);

        // loops must be created before loop items (texts and images)
        createLoops(doc, version);
        saveTexts(doc, version, language, user, SaveMode.CREATE);
        saveImages(doc, version, language, user, SaveMode.CREATE);
        saveMenus(doc, version, user, SaveMode.CREATE);

        saveTemplateNames(doc.getId(), doc.getTemplateNames());
        saveIncludes(doc.getId(), doc.getIncludesMap());
    }

    public void createCommonContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        VersionRef versionRef = doc.getVersionRef();
        Version version = findVersion(versionRef);
        User user = findUser(userDomainObject);

        createLoops(doc, version);
        saveMenus(doc, version, user, SaveMode.CREATE);
        saveTemplateNames(doc.getId(), doc.getTemplateNames());
        saveIncludes(doc.getId(), doc.getIncludesMap());
    }

    public void createI18nContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        Language language = findLanguage(docRef);
        User user = findUser(userDomainObject);

        saveTexts(doc, version, language, user, SaveMode.CREATE);
        saveImages(doc, version, language, user, SaveMode.CREATE);
    }

    /**
     * Updates existing document content.
     */
    public void updateContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = findVersion(docRef);
        Language language = findLanguage(docRef);
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
        saveImages(doc, version, language, user, SaveMode.UPDATE);
        saveMenus(doc, version, user, SaveMode.UPDATE);

        saveTemplateNames(doc.getId(), doc.getTemplateNames());
        saveIncludes(doc.getId(), doc.getIncludesMap());
    }

    private void createLoops(TextDocumentDomainObject textDocument, Version version) {
        textDocument.getLoops().forEach((loopNo, loopDO) -> {
            Loop loop = new Loop();
            List<Loop.Entry> items = new LinkedList<>();

            loopDO.getEntries().forEach((entryNo, enabled) -> items.add(new Loop.Entry(entryNo, enabled)));

            loop.setVersion(version);
            loop.setNo(loopNo);
            loop.setEntries(items);
            loop.setNextEntryNo(items.stream().mapToInt(Loop.Entry::getNo).max().orElse(1) + 1);

            loopRepository.save(loop);
        });
    }

    public void saveLoop(TextDocLoopContainer container) {
        Version version = findVersion(container);
        Integer id = loopRepository.findIdByVersionAndNo(version, container.getLoopNo());
        Loop loop = toJpaObject(container);
        loop.setId(id);

        loopRepository.save(loop);
    }

    /**
     * Saves existing document image.
     */
    public void saveImage(TextDocImageContainer container, UserDomainObject userDomainObject) {
        User user = findUser(userDomainObject);
        Image image = toJpaObject(container);

        saveImage(image, user, SaveMode.UPDATE);
    }

    public void saveImages(TextDocImagesContainer container, UserDomainObject userDomainObject) {
        User user = findUser(userDomainObject);
        Version version = findVersion(container);

        for (Map.Entry<com.imcode.imcms.api.DocumentLanguage, ImageDomainObject> e : container.getImages().entrySet()) {
            Language language = findLanguage(e.getKey());
            Image image = toJpaObject(e.getValue(), version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, user, SaveMode.UPDATE);
        }

        container.getImages().forEach((languageDO, imageDO) -> {
            Language language = findLanguage(languageDO);
            Image image = toJpaObject(imageDO, version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, user, SaveMode.UPDATE);
        });
    }

    public void saveText(TextDocTextContainer container, UserDomainObject userDomainObject) {
        User user = findUser(userDomainObject);
        Text text = toJpaObject(container);

        saveText(text, user, SaveMode.UPDATE);
    }

    public void saveTexts(TextDocTextsContainer container, UserDomainObject userDomainObject) {
        User user = findUser(userDomainObject);
        Version version = findVersion(container);

        container.getTexts().forEach((languageDO, textDO) -> {
            Language language = findLanguage(languageDO);
            Text text = toJpaObject(textDO, version, language, container.getTextNo(), toJpaObject(container.getLoopEntryRef()));

            saveText(text, user, SaveMode.UPDATE);
        });
    }

    private void saveTemplateNames(int docId, TextDocumentDomainObject.TemplateNames templateNamesDO) {
        TemplateNames templateNames = new TemplateNames();

        templateNames.setDocId(docId);
        templateNames.setDefaultTemplateName(templateNamesDO.getDefaultTemplateName());
        templateNames.setDefaultTemplateNameForRestricted1(templateNamesDO.getDefaultTemplateNameForRestricted1());
        templateNames.setDefaultTemplateNameForRestricted2(templateNamesDO.getDefaultTemplateNameForRestricted2());
        templateNames.setTemplateGroupId(templateNamesDO.getTemplateGroupId());
        templateNames.setTemplateName(templateNamesDO.getTemplateName());

        templateNamesRepository.save(templateNames);
    }

    private void saveIncludes(int docId, Map<Integer, Integer> includes) {
        includeRepository.deleteByDocId(docId);

        includes.forEach((no, includedDocId) -> {
            Include include = new Include();
            include.setId(null);
            include.setDocId(docId);
            include.setNo(no);
            include.setIncludedDocumentId(includedDocId);

            includeRepository.save(include);
        });
    }

    public void saveMenu(TextDocMenuContainer container, UserDomainObject userDomainObject) {
        VersionRef versionRef = container.getVersionRef();
        Version version = findVersion(versionRef);
        User user = findUser(userDomainObject);
        Menu menu = toJpaObject(container.getMenu(), version, container.getMenuNo());

        saveMenu(menu, user, SaveMode.UPDATE);

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

    private void saveMenus(TextDocumentDomainObject doc, Version version, User user, SaveMode saveMode) {
        doc.getMenus().forEach((menuNo, menuDO) -> {
            Menu menu = toJpaObject(menuDO, version, menuNo);
            saveMenu(menu, user, saveMode);
        });
    }

    private void saveMenu(Menu menu, User user, SaveMode saveMode) {
        if (saveMode == SaveMode.UPDATE) {
            Integer id = menuRepository.findIdByVersionAndNo(menu.getVersion(), menu.getNo());
            menu.setId(id);
        }

        menuRepository.saveAndFlush(menu);
        //fixme: save menu history
        //menuHistoryRepository.save(new MenuHistory(menu, user));
    }

    private void saveImages(TextDocumentDomainObject doc, Version version, Language language, User user, SaveMode saveMode) {
        for (Map.Entry<Integer, ImageDomainObject> entry : doc.getImages().entrySet()) {
            Image image = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveImage(image, user, saveMode);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> entry : doc.getLoopImages().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            Image image = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            saveImage(image, user, saveMode);
        }
    }

    private void saveTexts(TextDocumentDomainObject doc, Version version, Language language, User user, SaveMode saveMode) {
        for (Map.Entry<Integer, TextDomainObject> entry : doc.getTexts().entrySet()) {
            Text text = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveText(text, user, saveMode);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, TextDomainObject> entry : doc.getLoopTexts().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            Text text = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            saveText(text, user, saveMode);
        }
    }

    private void saveImage(Image image, User user, SaveMode saveMode) {
        if (saveMode == SaveMode.UPDATE) {
            LoopEntryRef loopEntryRef = image.getLoopEntryRef();
            Integer id = loopEntryRef == null
                    ? imageRepository.findIdByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(image.getVersion(), image.getLanguage(), image.getNo())
                    : imageRepository.findIdByVersionAndLanguageAndNoAndLoopEntryRef(image.getVersion(), image.getLanguage(), image.getNo(), loopEntryRef);

            image.setId(id);
        }

        createLoopEntryIfNotExists(image.getVersion(), image.getLoopEntryRef());
        imageRepository.save(image);
        imageHistoryRepository.save(new ImageHistory(image, user));
    }

    private void saveText(Text text, User user, SaveMode saveMode) {
        if (saveMode == SaveMode.UPDATE) {
            LoopEntryRef loopEntryRef = text.getLoopEntryRef();
            Integer id = loopEntryRef == null
                    ? textRepository.findIdByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(text.getVersion(), text.getLanguage(), text.getNo())
                    : textRepository.findIdByVersionAndLanguageAndNoAndLoopEntryRef(text.getVersion(), text.getLanguage(), text.getNo(), loopEntryRef);

            text.setId(id);
        }

        createLoopEntryIfNotExists(text.getVersion(), text.getLoopEntryRef());

        textRepository.save(text);
        textHistoryRepository.save(new TextHistory(text, user));
    }

    private void createLoopEntryIfNotExists(Version version, LoopEntryRef entryRef) {
        if (entryRef == null) return;

        Loop loop = loopRepository.findByVersionAndNo(
                version, entryRef.getLoopNo());
        int entryNo = entryRef.getEntryNo();
        int loopNo = entryRef.getLoopNo();

        if (loop == null) {
            loop = new Loop();
            loop.setVersion(version);
            loop.setNo(loopNo);
            loop.getEntries().add(new Loop.Entry(entryNo));
        } else {
            if (!loop.containsEntry(entryRef.getEntryNo())) {
                loop.getEntries().add(new Loop.Entry(entryNo));
            }
        }
        loop.setNextEntryNo(loop.getEntries().stream()
                .mapToInt(Loop.Entry::getNo).max().orElse(1) + 1);
        loopRepository.save(loop);
    }

    private Text toJpaObject(TextDocTextContainer container) {
        Language language = findLanguage(container);
        Version version = findVersion(container);
        LoopEntryRef loopEntryRef = toJpaObject(container.getLoopEntryRef());

        return toJpaObject(container.getText(), version, language, container.getTextNo(), loopEntryRef);
    }

    private Text toJpaObject(TextDomainObject textDO, Version version, Language language, int no, LoopEntryRef loopEntryRef) {
        Text text = new Text();

        text.setLanguage(language);
        text.setVersion(version);
        text.setNo(no);
        text.setText(textDO.getText());
        text.setType(TextType.values()[textDO.getType()]);
        text.setLoopEntryRef(loopEntryRef);

        return text;
    }

    private Image toJpaObject(TextDocImageContainer container) {
        Language language = findLanguage(container);
        Version version = findVersion(container);
        LoopEntryRef loopEntryRef = toJpaObject(container.getLoopEntryRef());

        return toJpaObject(container.getImage(), version, language, container.getImageNo(), loopEntryRef);
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
        image.setArchiveImageId(imageDO.getArchiveImageId());

        return image;
    }

    private LoopEntryRef toJpaObject(com.imcode.imcms.mapping.container.LoopEntryRef source) {
        return source == null
                ? null
                : new LoopEntryRef(source.getLoopNo(), source.getEntryNo());
    }

    private Loop toJpaObject(TextDocLoopContainer container) {
        return toJpaObject(container.getVersionRef(), container.getLoopNo(), container.getLoop());
    }

    private Loop toJpaObject(VersionRef versionRef, int loopNo, com.imcode.imcms.api.Loop loopDO) {
        List<Loop.Entry> entries = new LinkedList<>();
        Version version = findVersion(versionRef);

        loopDO.getEntries().forEach((entryNo, enabled) -> entries.add(new Loop.Entry(entryNo, enabled)));

        return Value.with(
                new Loop(),
                l -> {
                    l.setEntries(entries);
                    l.setNo(loopNo);
                    l.setNextEntryNo(loopDO.getNextEntryNo());
                    l.setVersion(version);
                }
        );
    }

    public void deleteText(TextDocumentDomainObject document, TextDocumentDomainObject.LoopItemRef entry) {
        final DocRef docRef = document.getRef();
        final Version version = findVersion(docRef);
        final Language language = findLanguage(docRef);
        final int loopNo = entry.getLoopNo();
        final int docId = document.getId();
        final LoopEntryRef loopEntryRef = new LoopEntryRef(loopNo, entry.getEntryNo());

        Text text = textRepository.findByVersionAndLanguageAndNoAndDocumentIdAndLoopEntryRef(
                version,
                language,
                loopNo,
                docId,
                loopEntryRef
        );

        textRepository.delete(text);
        textRepository.flush();

        document.updateLoopsContent();
    }

    public void deleteText(TextDocumentDomainObject document, TextDocTextContainer container) {
        final DocRef docRef = document.getRef();
        final Version version = findVersion(docRef);
        final Language language = findLanguage(container);
        final int textNo = toJpaObject(container).getNo();
        final int docId = document.getId();

        Text text = textRepository.findByVersionAndLanguageAndNoAndDocumentIdWhereLoopEntryRefIsNull(
                version,
                language,
                textNo,
                docId
        );

        textRepository.delete(text);
        textRepository.flush();
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

    private Language findLanguage(LanguageContainer container) {
        return languageRepository.findByCode(container.getLanguageCode());
    }

    private Language findLanguage(com.imcode.imcms.api.DocumentLanguage documentLanguage) {
        return languageRepository.findByCode(documentLanguage.getCode());
    }

    private enum SaveMode {
        CREATE, UPDATE
    }
}
