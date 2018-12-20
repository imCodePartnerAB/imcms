package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Image;

import java.util.List;
import java.util.Set;

public interface ImageService extends VersionedContentService, DeleterByDocumentId {

    ImageDTO getImage(ImageDTO dataHolder);

    ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    ImageDTO getPublicImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    List<Image> getUsedImagesInWorkingAndLatestVersions(String imageURL);

    Set<Image> getImagesAllVersionAndLanguages(int docId, Language language);

    void saveImage(ImageDTO imageDTO);

    void regenerateImages();

    /**
     * Returns a set of non-empty image links for latest document version and specified language
     */
    Set<String> getPublicImageLinks(int docId, Language language);

    void deleteImage(ImageDTO imageDTO);
}
