package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;

public interface ImageService {
    ImageDTO getImage(ImageDTO dataHolder);

    ImageDTO getImage(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRef);

    ImageDTO getPublicImage(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRef);

    void saveImage(ImageDTO imageDTO);
}
