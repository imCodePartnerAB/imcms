package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.TextDocImageContainer;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Map;

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


    // fixme: impl
    public void saveContent(TextDocumentDomainObject doc, UserDomainObject user) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        saveImages(doc, version, language);
    }


    public void saveImages(TextDocumentDomainObject doc, UserDomainObject user) {
        DocRef docRef = doc.getRef();
        Version version = versionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        saveImages(doc, version, language);
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





    private void saveImages(TextDocumentDomainObject doc, Version version, Language language) {
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

    }

    private void saveText(Text text) {
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
