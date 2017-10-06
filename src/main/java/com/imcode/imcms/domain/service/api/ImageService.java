package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Image;
import com.imcode.imcms.persistence.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Collection<Image> getAllGeneratedImages() {
        return imageRepository.findAllGeneratedImages();
    }

}
