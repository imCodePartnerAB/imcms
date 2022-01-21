package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.ImageHistoryRepository;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.util.Value;
import imcode.util.image.Format;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ImageDataInitializer extends TestDataCleaner {

    private final VersionService versionService;

    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;
    private final ImageHistoryRepository imageHistoryRepository;
    private final VersionDataInitializer versionDataInitializer;
    private final DocumentDataInitializer documentDataInitializer;

    public ImageDataInitializer(VersionService versionService, LanguageRepository languageRepository,
                                ImageRepository imageRepository,
                                ImageHistoryRepository imageHistoryRepository, VersionDataInitializer versionDataInitializer, DocumentDataInitializer documentDataInitializer) {
        super(imageRepository);
        this.versionService = versionService;
        this.languageRepository = languageRepository;
        this.imageRepository = imageRepository;
        this.imageHistoryRepository = imageHistoryRepository;
        this.versionDataInitializer = versionDataInitializer;
        this.documentDataInitializer = documentDataInitializer;
    }

    public ImageJPA createData(Integer imageIndex, Integer docId, Integer versionIndex) {
        final Version version = versionDataInitializer.createData(versionIndex, docId);
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, null);
    }

    public ImageJPA createData(Integer imageIndex, Version version) {
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, null);
    }

    public ImageJPA createData(Integer imageIndex, String fileName, String imgUrl, Version version) {
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, fileName, imgUrl, language, version, null);
    }

    public ImageJPA createData(int imageIndex, int docId, int versionIndex, LoopEntryRefJPA loopEntryRef) {
        final Version version = versionDataInitializer.createData(versionIndex, docId);
        final LanguageJPA language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, loopEntryRef);
    }

    public ImageJPA generateImage(int index, LanguageJPA language, Version version, LoopEntryRefJPA loopEntryRef) {
        return Value.with(new ImageJPA(), image -> {
            image.setIndex(index);
            image.setLanguage(language);
            image.setVersion(version);
            image.setLoopEntryRef(loopEntryRef);
            image.setFormat(Format.JPEG);
            imageRepository.save(image);
        });
    }

    public ImageHistoryJPA generateImageHistory(int index, LanguageJPA language, Version version, LoopEntryRefJPA loopEntryRef, User user) {
        return Value.with(new ImageHistoryJPA(), image -> {
            image.setIndex(index);
            image.setLanguage(language);
            image.setVersion(version);
            image.setLoopEntryRef(loopEntryRef);
            image.setFormat(Format.JPEG);
            image.setModifiedAt(LocalDateTime.now());
            image.setModifiedBy(user);
            imageHistoryRepository.save(image);
        });
    }

    public ImageJPA generateImage(int index, String fileName, String imgUrl, LanguageJPA language, Version version, LoopEntryRefJPA loopEntryRef) {
        return Value.with(new ImageJPA(), image -> {
            image.setIndex(index);
			image.setName(fileName);
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
        final ImageJPA imageLatest = createData(1, "", publishedImageURL, latestVersion);

        final Integer workingDocumentId = sameDoc ? commonDocumentDTO.getId() : documentDataInitializer.createData().getId();
        final Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentId);
        final ImageJPA imageWorking = createData(1, "", workingImageURL, workingVersion);
    }
}
