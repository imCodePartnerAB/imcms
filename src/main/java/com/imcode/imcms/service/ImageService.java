package com.imcode.imcms.service;

import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Image;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.09.17.
 */
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
