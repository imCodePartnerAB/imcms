package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Transactional
@Service("imageService")
class DefaultImageService extends AbstractVersionedContentService<Image, ImageRepository> implements ImageService {

    private final VersionService versionService;
    private final LanguageService languageService;
    private final TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage;
    private final Function<Image, ImageDTO> imageToImageDTO;

    DefaultImageService(ImageRepository imageRepository,
                        VersionService versionService,
                        LanguageService languageService,
                        TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage,
                        Function<Image, ImageDTO> imageToImageDTO) {

        super(imageRepository);
        this.versionService = versionService;
        this.languageService = languageService;
        this.imageDtoToImage = imageDtoToImage;
        this.imageToImageDTO = imageToImageDTO;
    }

    @Override
    public ImageDTO getImage(ImageDTO dataHolder) {

        if ((dataHolder.getIndex() == null) && dataHolder.isInText()) {
            return getEmptyDtoForTextEditor(dataHolder);
        }

        return getImage(
                dataHolder.getDocId(),
                dataHolder.getIndex(),
                dataHolder.getLangCode(),
                dataHolder.getLoopEntryRef()
        );
    }

    @Override
    public ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getImage(docId, index, langCode, loopEntryRef, versionService::getDocumentWorkingVersion);
    }

    @Override
    public ImageDTO getPublicImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getImage(docId, index, langCode, loopEntryRef, versionService::getLatestVersion);
    }

    @Override
    public void saveImage(ImageDTO imageDTO) {
        final Integer docId = imageDTO.getDocId();
        final Version version = versionService.getDocumentWorkingVersion(docId);
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(imageDTO.getLangCode()));

        generateImage(imageDTO);

        final Image image = imageDtoToImage.apply(imageDTO, version, language);
        final Integer imageId = getImageId(imageDTO, version, language);

        image.setId(imageId);
        repository.save(image);
        super.updateWorkingVersion(docId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        repository.deleteByDocId(docIdToDelete);
    }

    @Override
    public Set<String> getPublicImageLinks(int docId, Language language) {
        final Version latestVersion = versionService.getLatestVersion(docId);
        return repository.findNonEmptyImageLinkUrlByVersionAndLanguage(latestVersion, new LanguageJPA(language));
    }

    @Override
    public void deleteImage(ImageDTO imageDTO) {
        final Integer docId = imageDTO.getDocId();
        final Version version = versionService.getDocumentWorkingVersion(docId);
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(imageDTO.getLangCode()));

        final Integer imageId = getImageId(imageDTO, version, language);

        if (imageId != null) {
            final Image image = imageDtoToImage.apply(new ImageDTO(imageDTO), version, language);
            image.setId(imageId);

            repository.save(image);

            super.updateWorkingVersion(docId);
        }
    }

    private ImageDTO getEmptyDtoForTextEditor(ImageDTO dataHolder) {
        final ImageDTO emptyDTO = new ImageDTO(dataHolder);
        emptyDTO.setIndex(getFreeIndexForImageInTextEditor(dataHolder.getDocId()));

        return emptyDTO;
    }

    private Integer getFreeIndexForImageInTextEditor(Integer docId) {
        final int minIndex = Optional.ofNullable(repository.findMinIndexByVersion(docId)).orElse(0);
        return Math.min(minIndex, 0) - 1;
    }

    private ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef,
                              Function<Integer, Version> versionReceiver) {

        final Version version = versionReceiver.apply(docId);

        final LanguageJPA language = new LanguageJPA(languageService.findByCode(langCode));
        final Image image = getImage(index, version, language, loopEntryRef);

        return Optional.ofNullable(image)
                .map(imageToImageDTO)
                .orElse(new ImageDTO(index, docId, loopEntryRef, langCode));
    }

    private void generateImage(ImageDTO imageDTO) {
        String imagePath = imageDTO.getPath();

        if (StringUtils.isNotBlank(imagePath)) {
            imageDTO.setSource(ImcmsImageUtils.getImageSource(imagePath));
            imageDTO.setGeneratedFilename(ImcmsImageUtils.generateImageFileName(imageDTO));
            ImcmsImageUtils.generateImage(imageDTO, true);
        }
    }

    private Image getImage(int index, Version version, LanguageJPA language, LoopEntryRef loopEntryRef) {
        if (loopEntryRef == null) {
            return repository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index);

        } else {
            return repository.findByVersionAndLanguageAndIndexAndLoopEntryRef(
                    version, language, index, new LoopEntryRefJPA(loopEntryRef)
            );
        }
    }

    private Integer getImageId(ImageDTO imageDTO, Version version, LanguageJPA language) {
        final Integer index = imageDTO.getIndex();
        final LoopEntryRef loopEntryRefDTO = imageDTO.getLoopEntryRef();
        final Image image = getImage(index, version, language, loopEntryRefDTO);

        if (image == null) {
            return null;
        }

        return image.getId();
    }

    private Collection<Image> getAllGeneratedImages() {
        return repository.findAllGeneratedImages();
    }

    @PostConstruct
    private void regenerateImages() { // If generated images was cleared before start up
        getAllGeneratedImages().forEach((img) -> ImcmsImageUtils.generateImage(
                imageToImageDTO.apply(img), false)
        );
    }

    @Override
    protected Image removeId(Image image, Version version) {
        final Image newImage = new Image(image);
        newImage.setId(null);
        newImage.setVersion(version);
        return newImage;
    }
}
