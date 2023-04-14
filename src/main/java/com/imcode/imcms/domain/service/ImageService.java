package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Version;

import java.util.List;
import java.util.Set;

public interface ImageService extends VersionedContentService, DeleterByDocumentId {

    ImageDTO getImage(ImageDTO dataHolder);

    ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    ImageDTO getImage(int docId, int index, int versionNo, String langCode, LoopEntryRef loopEntryRef);

    ImageDTO getPublicImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

	List<ImageDTO> getLoopImages(int docId, String langCode, int loopIndex);

    List<ImageJPA> getUsedImagesInWorkingAndLatestVersions(String imageURL);

    List<ImageJPA> getByDocId(Integer docId);

    Set<ImageJPA> getImagesAllVersionAndLanguages(int docId, Language language);

	List<ImageDTO> getImagesByUrl(String url);

	List<ImageDTO> getImagesByFolderInUrl(String folder);

    void saveImage(ImageDTO imageDTO);

    void regenerateImages();

    /**
     * Returns a set of non-empty image links for latest document version and specified language
     */
    Set<String> getPublicImageLinks(int docId, Language language);

	/**
	 * Use only when you need to update some column in the database, e.g. url when rename folder or move file
	 * in all other cases, use {@link #saveImage(ImageDTO)}
	 */
	void updateImage(ImageDTO imageDTO);

    void deleteImage(ImageDTO imageDTO);
}
