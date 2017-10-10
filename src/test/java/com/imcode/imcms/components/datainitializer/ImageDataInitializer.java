package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.components.cleaner.RepositoryTestDataCleaner;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.repository.ImageRepository;
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

        final Image image = Value.with(new Image(), img -> {
            img.setIndex(imageIndex);
            img.setLanguage(languageRepository.findByCode("en"));
            img.setVersion(version);
            img.setFormat(Format.JPEG);
        });

        return imageRepository.save(image);
    }
}
