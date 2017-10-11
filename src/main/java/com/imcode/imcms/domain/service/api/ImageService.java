package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.LanguageService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final VersionService versionService;
    private final CommonContentService commonContentService;
    private final LanguageService languageService;
    private final TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage;
    private final Function<Image, ImageDTO> imageToImageDTO;

    public ImageService(ImageRepository imageRepository,
                        VersionService versionService,
                        CommonContentService commonContentService,
                        LanguageService languageService,
                        TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage,
                        Function<Image, ImageDTO> imageToImageDTO) {

        this.imageRepository = imageRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.languageService = languageService;
        this.imageDtoToImage = imageDtoToImage;
        this.imageToImageDTO = imageToImageDTO;
    }

    public Collection<Image> getAllGeneratedImages() {
        return imageRepository.findAllGeneratedImages();
    }

    public ImageDTO getImage(ImageDTO imageDTO) {
        final Integer docId = imageDTO.getDocId();
        final Integer index = imageDTO.getIndex();
        final LoopEntryRef loopEntryRef = imageDTO.getLoopEntryRef();
        final Version version = versionService.getDocumentWorkingVersion(docId);
        final int versionIndex = version.getNo();
        final UserDomainObject user = Imcms.getUser();
        final Language language = commonContentService.findByDocIdAndVersionNoAndUser(docId, versionIndex, user)
                .getLanguage();

        final Image image = (loopEntryRef == null)
                ? imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index)
                : imageRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, index, loopEntryRef);

        return Optional.ofNullable(image)
                .map(imageToImageDTO)
                .orElse(new ImageDTO(index, docId));
    }
}
