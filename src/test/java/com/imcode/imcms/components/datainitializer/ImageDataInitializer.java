package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.components.cleaner.RepositoryTestDataCleaner;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.util.Value;
import imcode.util.image.Format;
import org.springframework.stereotype.Component;

@Component
public class ImageDataInitializer extends RepositoryTestDataCleaner {

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

    public Image createData(int imageIndex, int docId, int versionIndex) {
        final Version version = versionDataInitializer.createData(versionIndex, docId);
        final Language language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, null);
    }

    public Image createData(int imageIndex, int docId, int versionIndex, LoopEntryRef loopEntryRef) {
        final Version version = versionDataInitializer.createData(versionIndex, docId);
        final Language language = languageRepository.findByCode("en");

        return generateImage(imageIndex, language, version, loopEntryRef);
    }

    public Image generateImage(int index, Language language, Version version, LoopEntryRef loopEntryRef) {
        return Value.with(new Image(), image -> {
            image.setIndex(index);
            image.setLanguage(language);
            image.setVersion(version);
            image.setLoopEntryRef(loopEntryRef);
            image.setFormat(Format.JPEG);
            imageRepository.save(image);
        });
    }
}
