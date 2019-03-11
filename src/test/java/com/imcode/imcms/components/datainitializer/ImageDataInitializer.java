package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.util.Value;
import imcode.util.image.Format;
import org.springframework.stereotype.Component;

@Component
public class ImageDataInitializer extends TestDataCleaner {

    private final CommonContentService commonContentService;
    private final VersionService versionService;

    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;
    private final VersionDataInitializer versionDataInitializer;
    private final DocumentDataInitializer documentDataInitializer;

    public ImageDataInitializer(CommonContentService commonContentService, VersionService versionService, LanguageRepository languageRepository,
                                ImageRepository imageRepository,
                                VersionDataInitializer versionDataInitializer, DocumentDataInitializer documentDataInitializer) {
        super(imageRepository);
        this.commonContentService = commonContentService;
        this.versionService = versionService;
        this.languageRepository = languageRepository;
        this.imageRepository = imageRepository;
        this.versionDataInitializer = versionDataInitializer;
        this.documentDataInitializer = documentDataInitializer;
    }

    public Image createData(Integer imageIndex, Integer docId, Integer versionIndex) {
        final Version version = versionDataInitializer.createData(versionIndex, docId);
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, null);
    }

    public Image createData(Integer imageIndex, Version version) {
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, null);
    }

    public Image createData(Integer imageIndex, String fileName, String imgUrl, Version version) {
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, fileName, imgUrl, language, version, null);
    }

    public Image createData(int imageIndex, int docId, int versionIndex, LoopEntryRefJPA loopEntryRef) {
        final Version version = versionDataInitializer.createData(versionIndex, docId);
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, loopEntryRef);
    }

    public Image generateImage(int index, LanguageJPA language, Version version, LoopEntryRefJPA loopEntryRef) {
        return Value.with(new Image(), image -> {
            image.setIndex(index);
            image.setLanguage(language);
            image.setVersion(version);
            image.setLoopEntryRef(loopEntryRef);
            image.setFormat(Format.JPEG);
            imageRepository.save(image);
        });
    }

    public Image generateImage(int index, String fileName, String imgUrl, LanguageJPA language, Version version, LoopEntryRefJPA loopEntryRef) {
        return Value.with(new Image(), image -> {
            image.setIndex(index);
            image.setLanguage(language);
            image.setVersion(version);
            image.setLoopEntryRef(loopEntryRef);
            image.setFormat(Format.JPEG);
            image.setUrl(imgUrl);
            imageRepository.save(image);
        });
    }

    public void createAllAvailableImageContent(boolean sameDoc, String workingImageURL, String publishedImageURL) {
        final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

        final Integer latestDocumentId = sameDoc ? commonDocumentDTO.getId() : documentDataInitializer.createData().getId();
        final Version latestVersion = versionService.create(latestDocumentId, 1);
        final Image imageLatest = createData(1, "", publishedImageURL, latestVersion);

        final Integer workingDocumentId = sameDoc ? commonDocumentDTO.getId() : documentDataInitializer.createData().getId();
        final Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentId);
        final Image imageWorking = createData(1, "", workingImageURL, workingVersion);
    }
}
