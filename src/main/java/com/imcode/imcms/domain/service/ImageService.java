package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.model.LoopEntryRef;

public interface ImageService extends VersionedContentService<ImageDTO> {
    ImageDTO getImage(ImageDTO dataHolder);

    ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    ImageDTO getPublicImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    void saveImage(ImageDTO imageDTO);
}
