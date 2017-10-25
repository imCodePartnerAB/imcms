package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final VersionService versionService;
    private final LanguageService languageService;
    private final String imagesPath;
    private final Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef;
    private final TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage;
    private final Function<Image, ImageDTO> imageToImageDTO;

    public ImageService(ImageRepository imageRepository,
                        VersionService versionService,
                        LanguageService languageService,
                        @Value("${ImageUrl}") String imagesPath,
                        Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef,
                        TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage,
                        Function<Image, ImageDTO> imageToImageDTO) {

        this.imageRepository = imageRepository;
        this.versionService = versionService;
        this.languageService = languageService;
        this.imagesPath = imagesPath;
        this.loopEntryRefDtoToLoopEntryRef = loopEntryRefDtoToLoopEntryRef;
        this.imageDtoToImage = imageDtoToImage;
        this.imageToImageDTO = imageToImageDTO;
    }

    public ImageDTO getImage(ImageDTO dataHolder) {
        return getImage(dataHolder.getDocId(), dataHolder.getIndex(), dataHolder.getLoopEntryRef());
    }

    public ImageDTO getImage(int docId, int index, LoopEntryRefDTO loopEntryRef) {
        return getImage(docId, index, loopEntryRef, versionService::getDocumentWorkingVersion);
    }

    public ImageDTO getPublicImage(int docId, int index, LoopEntryRefDTO loopEntryRef) {
        return getImage(docId, index, loopEntryRef, versionService::getLatestVersion);
    }

    public void saveImage(ImageDTO imageDTO) {
        final Version version = versionService.getDocumentWorkingVersion(imageDTO.getDocId());
        final Language language = languageService.findByCode(imageDTO.getLangCode());
        final Image image = generateImage(imageDTO, version, language);
        final Integer imageId = getImageId(imageDTO, version, language);

        image.setId(imageId);
        imageRepository.save(image);
    }

    private ImageDTO getImage(int docId, int index, LoopEntryRefDTO loopEntryRefDTO, Function<Integer, Version> versionReceiver) {
        final Version version = versionReceiver.apply(docId);
        final Language language = languageService.getCurrentUserLanguage();
        final Image image = getImage(index, version, language, loopEntryRefDTO);

        return Optional.ofNullable(image)
                .map(imageToImageDTO)
                .orElse(new ImageDTO(index, docId));
    }

    private Image generateImage(ImageDTO imageDTO, Version version, Language language) {
        ImageSource imageSource = new NullImageSource();
        String imagePath = imageDTO.getPath();

        if (StringUtils.isNotBlank(imagePath)) {

            if (imagePath.startsWith(imagesPath)) {
                imagePath = imagePath.substring(imagesPath.length());
            }

            if (StringUtils.isNotBlank(imagePath)) {
                imageSource = new ImagesPathRelativePathImageSource(imagePath);
            }

            imageDTO.setSource(imageSource);
            imageDTO.generateFilename();
            ImcmsImageUtils.generateImage(imageDTO, true);
        }

        return imageDtoToImage.apply(imageDTO, version, language);
    }

    private Image getImage(int index, Version version, Language language, LoopEntryRefDTO loopEntryRefDTO) {
        final LoopEntryRef loopEntryRef = loopEntryRefDtoToLoopEntryRef.apply(loopEntryRefDTO);

        return (loopEntryRef == null)
                ? imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index)
                : imageRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, index, loopEntryRef);
    }

    private Integer getImageId(ImageDTO imageDTO, Version version, Language language) {
        final Integer index = imageDTO.getIndex();
        final LoopEntryRefDTO loopEntryRefDTO = imageDTO.getLoopEntryRef();
        final Image image = getImage(index, version, language, loopEntryRefDTO);

        if (image == null) {
            return null;
        }

        return image.getId();
    }

    private Collection<Image> getAllGeneratedImages() {
        return imageRepository.findAllGeneratedImages();
    }

    @PostConstruct
    private void regenerateImages() { // If generated images was cleared before start up
        getAllGeneratedImages().forEach((img) -> ImcmsImageUtils.generateImage(
                ImcmsImageUtils.toDomainObject(img), false)
        );
    }
}
