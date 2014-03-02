package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.DocVersionRef;
import com.imcode.imcms.mapping.container.TextDocImageContainer;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
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
    private EntityConverter entityConverter;

    @Inject
    private DocumentGetter menuItemDocumentGetter;

    public Map<Integer, TextDomainObject> getTexts(final DocRef docRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        final Map<Integer, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByDocVersionAndLanguageAndLoopEntryRefIsNull(version, language)) {
            result.put(text.getNo(), toDomainObject(text));
        }

        return result;
    }

    public Map<TextDocumentDomainObject.LoopItemRef, TextDomainObject> getLoopTexts(DocRef docRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
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
        DocVersion version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, TextDomainObject> result = new HashMap<>();

        for (Text text : textRepository.findByDocVersionAndNoAndLoopEntryRefIsNull(version, textNo)) {
            result.put(entityConverter.fromEntity(text.getLanguage()), toDomainObject(text));
        }

        return result;
    }

    public Map<DocumentLanguage, TextDomainObject> getLoopTexts(DocVersionRef docVersionRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, TextDomainObject> result = new HashMap<>();
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Text text : textRepository.findByDocVersionAndNoAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(entityConverter.fromEntity(text.getLanguage()), toDomainObject(text));
        }

        return result;
    }

    public TextDomainObject getText(DocRef docRef, int textNo) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        return toDomainObject(
                textRepository.findByDocVersionAndLanguageAndNoAndLoopEntryIsNull(version, language, textNo)
        );
    }

    public TextDomainObject getLoopText(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return toDomainObject(
                textRepository.findByDocVersionAndLanguageAndNoAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }

    public Map<Integer, ImageDomainObject> getImages(DocRef docRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        final Map<Integer, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByDocVersionAndLanguageAndLoopEntryRefIsNull(version, language)) {
            result.put(image.getNo(), toDomainObject(image));
        }

        return result;
    }

    public Map<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> getLoopImages(DocRef docRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
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
        DocVersion version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, ImageDomainObject> result = new HashMap<>();

        for (Image image : imageRepository.findByDocVersionAndNoAndLoopEntryRefIsNull(version, textNo)) {
            result.put(entityConverter.fromEntity(image.getLanguage()), toDomainObject(image));
        }

        return result;
    }

    public Map<DocumentLanguage, ImageDomainObject> getLoopImages(DocVersionRef docVersionRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        Map<DocumentLanguage, ImageDomainObject> result = new HashMap<>();
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        for (Image image : imageRepository.findByDocVersionAndNoAndLoopEntryRef(version, loopItemRef.getItemNo(), loopEntryRef)) {
            result.put(entityConverter.fromEntity(image.getLanguage()), toDomainObject(image));
        }

        return result;
    }


    public ImageDomainObject getImage(DocRef docRef, int textNo) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        return toDomainObject(
                imageRepository.findByDocVersionAndLanguageAndNoAndLoopEntryRefIsNull(version, language, textNo)
        );
    }

    public ImageDomainObject getLoopImage(DocRef docRef, TextDocumentDomainObject.LoopItemRef loopItemRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());
        LoopEntryRef loopEntryRef = new LoopEntryRef(loopItemRef.getLoopNo(), loopItemRef.getEntryNo());

        return toDomainObject(
                imageRepository.findByDocVersionAndLanguageAndNoAndLoopEntryRef(version, language, loopItemRef.getEntryNo(), loopEntryRef)
        );
    }


    public void saveImage(TextDocImageContainer imageContainer) {
        DocVersion docVersion = versionRepository.findByDocIdAndNo(imageContainer.getDocRef().getDocId(), imageContainer.getDocRef().getDocVersionNo());
        Language language = languageRepository.findByCode(imageContainer.getDocRef().getDocLanguageCode());

        ImageDomainObject image = imageContainer.getImage();

        Image jpaImage = toJpaObject(image);

        jpaImage.setNo(imageContainer.getImageNo());
        jpaImage.setDocVersion(docVersion);
        jpaImage.setLanguage(language);

        com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef = imageContainer.getLoopEntryRef();
        if (loopEntryRef != null) {
            jpaImage.setLoopEntryRef(new LoopEntryRef(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo()));
        }

        imageRepository.save(jpaImage);

        // fixme:  history
        // TextDocImageHistory textDocImageHistory = new TextDocImageHistory(image, user);
        // textDocRepository.saveImageHistory(textDocImageHistory);
    }


    public Map<Integer, Loop> getLoops(DocVersionRef docVersionRef) {
        DocVersion version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());

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
        DocVersion version = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());

        return toApiObject(loopRepository.findByDocVersionAndNo(version, loopNo));
    }


    public void addLoopEntry(DocRef docRef, com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef) {
        DocVersion docVersion = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop jpaLoop =
                loopRepository.findByDocVersionAndNo(docVersion, loopEntryRef.getLoopNo());

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
        DocVersion docVersion = versionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());
        List<Menu> textDocMenus = menuRepository.getByDocVersion(docVersion);
        Map<Integer, MenuDomainObject> menus = new HashMap<>();

        for (Menu menu : textDocMenus) {
            menus.put(menu.getNo(), toDomainObject(menu));
        }

        return menus;
    }


    public TextDocumentDomainObject.TemplateNames getTemplateNames(int docId) {
        return entityConverter.fromEntity(templateNamesRepository.findOne(docId));
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

    private MenuDomainObject toDomainObject(Menu jpaMenu) {
        MenuDomainObject menu = new MenuDomainObject();

        menu.setSortOrder(jpaMenu.getSortOrder());

        for (Map.Entry<Integer, MenuItem> e : jpaMenu.getItems().entrySet()) {
            MenuItem jpaMenuItem = e.getValue();
            Integer referencedDocumentId = e.getKey();
            MenuItemDomainObject menuItemDomainObject = new MenuItemDomainObject();
            GetterDocumentReference gtr = new GetterDocumentReference(referencedDocumentId, menuItemDocumentGetter);

            menuItemDomainObject.setDocumentReference(gtr);
            menuItemDomainObject.setSortKey(jpaMenuItem.getSortKey());
            menuItemDomainObject.setTreeSortIndex(jpaMenuItem.getTreeSortIndex());

            menu.addMenuItemUnchecked(menuItemDomainObject);
        }

        return menu;
    }



    public ImageDomainObject toDomainObject(Image jpaImage) {
        if (jpaImage == null) return null;

        ImageDomainObject imageDO = new ImageDomainObject();

        imageDO.setAlign(jpaImage.getAlign());
        imageDO.setAlternateText(jpaImage.getAlternateText());
        //fixme: check
        //imageDO.setArchiveImageId();
        imageDO.setBorder(jpaImage.getBorder());
        //imageDO.setCropRegion();
        imageDO.setGeneratedFilename(jpaImage.getGeneratedFilename());
        imageDO.setHeight(jpaImage.getHeight());
        imageDO.setHorizontalSpace(jpaImage.getHorizontalSpace());
        imageDO.setLinkUrl(jpaImage.getLinkUrl());
        imageDO.setLowResolutionUrl(jpaImage.getLowResolutionUrl());
        imageDO.setName(jpaImage.getName());
        //imageDO.setResize();
        imageDO.setTarget(jpaImage.getTarget());
        imageDO.setVerticalSpace(jpaImage.getVerticalSpace());
        imageDO.setWidth(jpaImage.getWidth());

        return initImageSource(jpaImage, imageDO);
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



    //todo: set null vs
    public Image toJpaObject(ImageDomainObject imageDO) {
        Image jpaImage = new Image();
        jpaImage.setAlign(imageDO.getAlign());
        jpaImage.setAlternateText(imageDO.getAlternateText());
        jpaImage.setBorder(imageDO.getBorder());
        //e.setCropRegion()
        jpaImage.setFormat(imageDO.getFormat() == null ? 0 : imageDO.getFormat().getOrdinal());
        jpaImage.setGeneratedFilename(imageDO.getGeneratedFilename());
        jpaImage.setHeight(imageDO.getHeight());
        jpaImage.setHorizontalSpace(imageDO.getHorizontalSpace());
        jpaImage.setUrl(imageDO.getSource().toStorageString());
        jpaImage.setLinkUrl(imageDO.getLinkUrl());

        jpaImage.setLowResolutionUrl(imageDO.getLowResolutionUrl());
        jpaImage.setName(imageDO.getName());
        jpaImage.setResize(imageDO.getResize() == null ? 0 : imageDO.getResize().getOrdinal());
        jpaImage.setRotateAngle(imageDO.getRotateDirection() == null ? 0 : imageDO.getRotateDirection().getAngle());
        jpaImage.setTarget(imageDO.getTarget());
        jpaImage.setType(imageDO.getSource().getTypeId());
        jpaImage.setVerticalSpace(imageDO.getVerticalSpace());
        jpaImage.setWidth(imageDO.getWidth());
        jpaImage.setHeight(imageDO.getHeight());

        //e.setNo(image.get)
        //e.setDocVersion()
        //e.setId()
        //e.setLanguage()

        return jpaImage;
    }

//  public RotateDirection getRotateDirection() {
//    return RotateDirection.getByAngleDefaultIfNull(rotateAngle);
//  }
//
//  public void setRotateDirection(RotateDirection dir) {
//    this.rotateAngle = (short) (dir != null ? dir.getAngle() : 0);
//  }

}
