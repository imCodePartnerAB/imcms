package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Loop;
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

    private enum SaveMode {
        CREATE, UPDATE
    }

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
    private DocumentGetter menuItemDocumentGetter;

    @Inject
    private DocumentLanguageMapper languageMapper;

    @Inject
    private UserRepository userRepository;


    /**
     * Saves new document content.
     *
     * @param doc
     * @param userDomainObject
     */
    public void createContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        User user = userRepository.getOne(userDomainObject.getId());

        // loops must be created before loop items (texts and images)
        createLoops(doc, version);
        saveTexts(doc, version, language, user, SaveMode.CREATE);
        saveImages(doc, version, language, user, SaveMode.CREATE);
        saveMenus(doc, version, user, SaveMode.CREATE);

        saveTemplateNames(doc.getId(), doc.getTemplateNames());
        saveIncludes(doc.getId(), doc.getIncludesMap());
    }

    public void createSharedContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        User user = userRepository.getOne(userDomainObject.getId());

        createLoops(doc, version);
        saveMenus(doc, version, user, SaveMode.CREATE);
        saveTemplateNames(doc.getId(), doc.getTemplateNames());
        saveIncludes(doc.getId(), doc.getIncludesMap());
    }

    public void createI18nContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        User user = userRepository.getOne(userDomainObject.getId());

        saveTexts(doc, version, language, user, SaveMode.CREATE);
        saveImages(doc, version, language, user, SaveMode.CREATE);
    }

    /**
     * Updates existing document content.
     *
     * @param doc
     * @param userDomainObject
     */
    public void updateContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getId(), docRef.getVersionNo());
        Language language = languageRepository.findByCode(docRef.getLanguageCode());
        User user = userRepository.getOne(userDomainObject.getId());

        // loop items must be deleted before loops (texts and images)
        textRepository.deleteByVersionAndLanguage(version, language);
        imageRepository.deleteByVersionAndLanguage(version, language);
        menuRepository.deleteByVersion(version);
        // loops must be re-created before loop items (texts and images)
        loopRepository.deleteByVersion(version);

        createLoops(doc, version);
        saveTexts(doc, version, language, user, SaveMode.UPDATE);
        saveImages(doc, version, language, user, SaveMode.UPDATE);
        saveMenus(doc, version, user, SaveMode.UPDATE);

        saveTemplateNames(doc.getId(), doc.getTemplateNames());
        saveIncludes(doc.getId(), doc.getIncludesMap());
    }

    private void createLoops(TextDocumentDomainObject textDocument, Version version) {
        textDocument.getLoops().forEach((loopNo, loopDO) -> {
            com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop loop = new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop();
            List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry> items = new LinkedList<>();

            loopDO.getEntries().forEach((entryNo, enabled) -> {
                items.add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(entryNo, enabled));
            });

            loop.setVersion(version);
            loop.setNo(loopNo);
            loop.setEntries(items);
            loop.setNextEntryNo(items.stream().mapToInt(i -> i.getNo()).max().orElse(0));

            loopRepository.save(loop);
        });
    }

    public void saveLoop(TextDocLoopContainer container) {
        Version version = versionRepository.findByDocIdAndNo(container.getDocId(), container.getVersionNo());
        Integer id = loopRepository.findIdByVersionAndNo(version, container.getLoopNo());
        com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop loop = toJpaObject(container);
        loop.setId(id);

        loopRepository.save(loop);
    }

    /**
     * Saves existing document image.
     *
     * @param container
     * @param userDomainObject
     */
    public void saveImage(TextDocImageContainer container, UserDomainObject userDomainObject) {
        User user = userRepository.getOne(userDomainObject.getId());
        Image image = toJpaObject(container);

        saveImage(image, user, SaveMode.UPDATE);
    }

    public void saveImages(TextDocImagesContainer container, UserDomainObject userDomainObject) {
        User user = userRepository.getOne(userDomainObject.getId());
        Version version = versionRepository.findByDocIdAndNo(container.getDocId(), container.getVersionNo());

        for (Map.Entry<com.imcode.imcms.api.DocumentLanguage, ImageDomainObject> e : container.getImages().entrySet()) {
            Language language = languageRepository.findByCode(e.getKey().getCode());
            Image image = toJpaObject(e.getValue(), version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, user, SaveMode.UPDATE);
        }

        container.getImages().forEach((languageDO, imageDO) -> {
            Language language = languageRepository.findByCode(languageDO.getCode());
            Image image = toJpaObject(imageDO, version, language, container.getImageNo(), toJpaObject(container.getLoopEntryRef()));

            saveImage(image, user, SaveMode.UPDATE);
        });
    }

    public void saveText(TextDocTextContainer container, UserDomainObject userDomainObject) {
        User user = userRepository.getOne(userDomainObject.getId());
        Text text = toJpaObject(container);

        saveText(text, user, SaveMode.UPDATE);
    }

    public void saveTexts(TextDocTextsContainer container, UserDomainObject userDomainObject) {
        User user = userRepository.getOne(userDomainObject.getId());
        Version version = versionRepository.findByDocIdAndNo(container.getDocId(), container.getVersionNo());

        container.getTexts().forEach((languageDO, textDO) -> {
            Language language = languageRepository.findByCode(languageDO.getCode());
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
        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getNo());
        User user = userRepository.getOne(userDomainObject.getId());
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

        com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop loop = loopRepository.findByVersionAndNo(
                version, entryRef.getLoopNo());

        int entryNo = entryRef.getEntryNo();

        if (loop == null) {
            loop = new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop();
            loop.setNextEntryNo(entryNo + 1);
            loop.setVersion(version);
            loop.setNo(entryRef.getLoopNo());
            loop.getEntries().add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(entryNo));

            loopRepository.save(loop);
        } else {
            if (!loop.containsEntry(entryRef.getEntryNo())) {
                loop.getEntries().add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(entryNo));
                loop.setNextEntryNo(Math.max(loop.getNextEntryNo(), entryNo + 1));

                loopRepository.save(loop);
            }
        }
    }

    private Text toJpaObject(TextDocTextContainer container) {
        Language language = languageRepository.findByCode(container.getLanguageCode());
        Version version = versionRepository.findByDocIdAndNo(container.getDocId(), container.getVersionNo());
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
        Language language = languageRepository.findByCode(container.getLanguageCode());
        Version version = versionRepository.findByDocIdAndNo(container.getDocId(), container.getVersionNo());
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

    private com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop toJpaObject(TextDocLoopContainer container) {
        return toJpaObject(container.getVersionRef(), container.getLoopNo(), container.getLoop());
    }

    //fixme: assign version, check existing
    private com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop toJpaObject(VersionRef versionRef, int loopNo, Loop loopDO) {
        List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry> entries = new LinkedList<>();

        loopDO.getEntries().forEach((entryNo, enabled) -> {
            entries.add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(entryNo, enabled));
        });

        return Value.with(
                new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop(),
                l -> {
                    l.setEntries(entries);
                    l.setNo(loopNo);
                }
        );
    }

}
