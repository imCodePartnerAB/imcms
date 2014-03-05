package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopEntryRef;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//fixme: before saving loop items - create loops/entries if required
@Service
@Transactional
public class TextDocumentContentSaver {

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

    @Inject
    private UserRepository userRepository;


    /**
     * Saves existing document content.
     * @param doc
     * @param userDomainObject
     */
    public void saveContent(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        User user = userRepository.getOne(userDomainObject.getId());

        saveLoops(doc, version, user);
        saveTexts(doc, version, language, user);
        saveImages(doc, version, language, user);
        saveMenus(doc, version, user);

        saveTemplateNames(doc, userDomainObject);
        saveIncludes(doc, userDomainObject);
    }

    // private ???
    public void saveLoops(TextDocumentDomainObject textDocument, UserDomainObject userDomainObject) {
        DocVersionRef versionRef = textDocument.getVersionRef();
        User user = userRepository.getOne(userDomainObject.getId());

        Version version = versionRepository.findByDocIdAndNo(versionRef.getDocId(), versionRef.getDocVersionNo());
        List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop> loops = loopRepository.findByDocVersion(version);
        loopRepository.delete(loops);

        for (Map.Entry<Integer, Loop> loopAndNo : textDocument.getLoops().entrySet()) {
            Loop loop = loopAndNo.getValue();
            com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop ormLoop = new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop();
            List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry> ormItems = new LinkedList<>();

            for (Map.Entry<Integer, Boolean> loopEntry : loop.getEntries().entrySet()) {
                ormItems.add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(loopEntry.getKey(), loopEntry.getValue()));
            }

            ormLoop.setNo(loopAndNo.getKey());
            ormLoop.setEntries(ormItems);

            loopRepository.save(ormLoop);
        }
    }

    private void saveLoops(TextDocumentDomainObject textDocument, Version version, User user) {
        loopRepository.delete(loopRepository.findByDocVersion(version));

        for (Map.Entry<Integer, Loop> loopAndNo : textDocument.getLoops().entrySet()) {
            Loop loopDO = loopAndNo.getValue();
            com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop loop = new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop();
            List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry> items = new LinkedList<>();

            for (Map.Entry<Integer, Boolean> loopDOEntry : loopDO.getEntries().entrySet()) {
                items.add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(loopDOEntry.getKey(), loopDOEntry.getValue()));
            }

            loop.setVersion(version);
            loop.setNo(loopAndNo.getKey());
            loop.setEntries(items);

            loopRepository.save(loop);
        }
    }

    /**
     * Saves existing document image.
     * @param imageContainer
     * @param userDomainObject
     */
    //fixme: create enclosing loop
    public void saveImage(TextDocImageContainer imageContainer, UserDomainObject userDomainObject) {
        User user = userRepository.getOne(userDomainObject.getId());
        Image image = toJpaObject(imageContainer);

        saveImage(image, user);
    }

    //fixme: create enclosing loop
    public void saveText(TextDocTextContainer textContainer,  UserDomainObject userDomainObject) {
        User user = userRepository.getOne(userDomainObject.getId());
        Text text = toJpaObject(textContainer);

        saveText(text, user);
    }


    /**
     * Saves existing document images.
     * @param doc
     * @param userDomainObject
     */
    //fixme: create enclosing loop
    public void saveImages(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        User user = userRepository.getOne(userDomainObject.getId());

        saveImages(doc, version, language, user);
    }

    //fixme: create enclosing loop
    public void saveTexts(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        User user = userRepository.getOne(userDomainObject.getId());

        saveTexts(doc, version, language, user);
    }


    public void saveIncludes(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        int docId = doc.getId();

        includeRepository.deleteByDocId(docId);

        for (Map.Entry<Integer, Integer> entry : doc.getIncludesMap().entrySet()) {
            Include include = new Include();
            include.setId(null);
            include.setDocId(docId);
            include.setNo(entry.getKey());
            include.setIncludedDocumentId(entry.getValue());

            includeRepository.save(include);
        }
    }

    public void saveTemplateNames(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        saveTemplateNames(doc.getId(), doc.getTemplateNames());
    }


    public void saveTemplateNames(int docId, TextDocumentDomainObject.TemplateNames templateNamesDO) {
        TemplateNames templateNames = new TemplateNames();

        templateNames.setDocId(docId);
        templateNames.setDefaultTemplateName(templateNamesDO.getDefaultTemplateName());
        templateNames.setDefaultTemplateNameForRestricted1(templateNamesDO.getDefaultTemplateNameForRestricted1());
        templateNames.setDefaultTemplateNameForRestricted2(templateNamesDO.getDefaultTemplateNameForRestricted2());
        templateNames.setTemplateGroupId(templateNamesDO.getTemplateGroupId());
        templateNames.setTemplateName(templateNamesDO.getTemplateName());

        templateNamesRepository.save(templateNames);
    }

    public void saveMenus(TextDocumentDomainObject doc, UserDomainObject userDomainObject) {
        DocVersionRef docVersionRef = doc.getVersionRef();
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        User user = userRepository.getOne(userDomainObject.getId());

        saveMenus(doc, version, user);
    }

    // fixme: try replce
    public void saveMenu(TextDocMenuContainer container, UserDomainObject userDomainObject) {
        DocVersionRef docVersionRef = container.getDocVersionRef();
        Version version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        User user = userRepository.getOne(userDomainObject.getId());
        Menu menu = toJpaObject(container.getMenu(), version, container.getMenuNo());

        saveMenu(menu, user);

    }

    private Menu toJpaObject(MenuDomainObject menuDO, Version version, int no) {
        Menu menu = new Menu();
        Map<Integer, MenuItem> menuItems = new HashMap<>();

        for (Map.Entry<Integer, MenuItemDomainObject> e : menuDO.getItemsMap().entrySet()) {
            MenuItem menuItem = new MenuItem();
            menuItem.setSortKey(e.getValue().getSortKey());
            menuItem.setTreeSortIndex(e.getValue().getTreeSortIndex());
            menuItems.put(e.getKey(), menuItem);
        }

        menu.setVersion(version);
        menu.setNo(no);
        menu.setSortOrder(menuDO.getSortOrder());
        menu.setItems(menuItems);

        return menu;
    }


    private void saveMenus(TextDocumentDomainObject doc, Version version, User user) {
        menuRepository.deleteByDocVersion(version);

        for (Map.Entry<Integer, MenuDomainObject> entry : doc.getMenus().entrySet()) {
            Menu menu = toJpaObject(entry.getValue(), version, entry.getKey());
            saveMenu(menu, user);
        }
    }

    private void saveMenu(Menu menu, User user) {
        menuRepository.save(menu);

        // fixme: history
        // TextDocMenuHistory menuHistory = new TextDocMenuHistory(menu, user);
        // textDocRepository.saveMenuHistory(menuHistory);
    }



    private void saveImages(TextDocumentDomainObject doc, Version version, Language language, User user) {
        imageRepository.deleteByVersionAndLanguage(version, language);

        for (Map.Entry<Integer, ImageDomainObject> entry : doc.getImages().entrySet()) {
            Image image = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveImage(image, user);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> entry : doc.getLoopImages().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            Image image = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            saveImage(image, user);
        }
    }


    private void saveTexts(TextDocumentDomainObject doc, Version version, Language language, User user) {
        textRepository.deleteByVersionAndLanguage(version, language);

        for (Map.Entry<Integer, TextDomainObject> entry : doc.getTexts().entrySet()) {
            Text text = toJpaObject(entry.getValue(), version, language, entry.getKey(), null);

            saveText(text, user);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, TextDomainObject> entry : doc.getLoopTexts().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

            Text text = toJpaObject(entry.getValue(), version, language, loopItemRef.getItemNo(), loopEntryRef);

            saveText(text, user);
        }
    }


    private void saveImage(Image image, User user) {
        imageRepository.save(image);

        // fixme: save history
        // TextDocImageHistory textDocImageHistory = new TextDocImageHistory(image, user);
        // textDocRepository.saveImageHistory(textDocImageHistory);
    }



    private void saveText(Text text, User user) {
        textRepository.save(text);

        // fixme: history
        //TextDocTextHistory textHistory = new TextDocTextHistory(textRef, user);
        //textRepository.saveTextHistory(textHistory);
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
            //fixme:
            Loop apiLoop = null;//toApiObject(jpaLoop);
            int contentNo = loopEntryRef.getEntryNo();
            if (!apiLoop.findEntryIndexByNo(contentNo).isPresent()) {
                jpaLoop.getEntries().add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(contentNo));
                loopRepository.save(jpaLoop);
            }
        }

    }



    private Text toJpaObject(TextDocTextContainer container) {
        Language language = languageRepository.findByCode(container.getDocLanguageCode());
        Version version = versionRepository.findByDocIdAndNo(container.getDocId(), container.getDocVersionNo());
        LoopEntryRef loopEntryRef = toJpaObject(container.getLoopEntryRef());
        Text text = toJpaObject(container.getText(), version, language, container.getTextNo(), loopEntryRef);
        Integer textId = loopEntryRef == null
                ? textRepository.findIdByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(version, language, container.getTextNo())
                : textRepository.findIdByVersionAndLanguageAndNoAndLoopEntryRef(version, language, container.getTextNo(), loopEntryRef);

        text.setId(textId);

        return text;
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
        Language language = languageRepository.findByCode(container.getDocLanguageCode());
        Version version = versionRepository.findByDocIdAndNo(container.getDocId(), container.getDocVersionNo());
        LoopEntryRef loopEntryRef = toJpaObject(container.getLoopEntryRef());
        Image image = toJpaObject(container.getImage(), version, language, container.getImageNo(), loopEntryRef);
        Integer imageId = loopEntryRef == null
                ? imageRepository.findIdByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(version, language, container.getImageNo())
                : imageRepository.findIdByVersionAndLanguageAndNoAndLoopEntryRef(version, language, container.getImageNo(), loopEntryRef);

        image.setId(imageId);

        return image;
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



    private LoopEntryRef toJpaObject(com.imcode.imcms.mapping.container.LoopEntryRef source) {
        return source == null
                ? null
                : new LoopEntryRef(source.getLoopNo(), source.getEntryNo());
    }

}
