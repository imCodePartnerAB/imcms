package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final VersionService versionService;

    public ImageService(ImageRepository imageRepository, VersionService versionService) {
        this.imageRepository = imageRepository;
        this.versionService = versionService;
    }

    public Collection<Image> getAllGeneratedImages() {
        return imageRepository.findAllGeneratedImages();
    }

    public Image getImage(int docId, int index) {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(docId);
        //todo: implement!
        return null;
    }
}
