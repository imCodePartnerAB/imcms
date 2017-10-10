package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.repository.ImageRepository;
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
    private final Function<Image, ImageDTO> imageToImageDTO;

    public ImageService(ImageRepository imageRepository,
                        VersionService versionService,
                        CommonContentService commonContentService,
                        Function<Image, ImageDTO> imageToImageDTO) {

        this.imageRepository = imageRepository;
        this.versionService = versionService;
        this.commonContentService = commonContentService;
        this.imageToImageDTO = imageToImageDTO;
    }

    public Collection<Image> getAllGeneratedImages() {
        return imageRepository.findAllGeneratedImages();
    }

    public ImageDTO getImage(int docId, int index) {
        final Version version = versionService.getDocumentWorkingVersion(docId);
        final int versionIndex = version.getNo();
        final UserDomainObject user = Imcms.getUser();
        final Language language = commonContentService.findByDocIdAndVersionNoAndUser(docId, versionIndex, user)
                .getLanguage();

        final Image image = imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index);

        return Optional.ofNullable(image)
                .map(imageToImageDTO)
                .orElse(new ImageDTO(index, docId));
    }
}
