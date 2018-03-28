package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageInTextDTO;
import com.imcode.imcms.persistence.repository.ImageRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by Serhii from Ubrainians for imCode
 * on 14.02.2018.
 */
@Component
public class ImageInTextFactory {

    private final ImageRepository repository;

    ImageInTextFactory(ImageRepository imageRepository) {
        this.repository = imageRepository;
    }

    public ImageInTextDTO createWithIndex(ImageDTO dataHolder) {
        final ImageInTextDTO emptyDTO = new ImageInTextDTO(dataHolder);
        emptyDTO.setIndex(getFreeIndexForImageInTextEditor(dataHolder.getDocId()));
        return emptyDTO;
    }

    private Integer getFreeIndexForImageInTextEditor(Integer docId) {
        final int minIndex = Optional.ofNullable(repository.findMinIndexByVersion(docId)).orElse(0);
        return Math.min(minIndex, 0) - 1;
    }

}
