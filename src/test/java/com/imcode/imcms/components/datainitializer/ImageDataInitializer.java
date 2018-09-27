package com.imcode.imcms.components.datainitializer;

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

    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;
    private final VersionDataInitializer versionDataInitializer;

    public ImageDataInitializer(LanguageRepository languageRepository,
                                ImageRepository imageRepository,
                                VersionDataInitializer versionDataInitializer) {
        super(imageRepository);
        this.languageRepository = languageRepository;
        this.imageRepository = imageRepository;
        this.versionDataInitializer = versionDataInitializer;
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
}
