package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.factory.ImageInTextFactory;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.ImageHistoryService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;

@Transactional
@Service("imageService")
class DefaultImageService extends AbstractVersionedContentService<ImageJPA, ImageRepository> implements ImageService {

    private final VersionService versionService;
    private final LanguageService languageService;
    private final ImageHistoryService imageHistoryService;
    private final TernaryFunction<ImageDTO, Version, Language, ImageJPA> imageDTOToImageJPA;
    private final ImageInTextFactory imageInTextFactory;
    private final Function<ImageJPA, ImageDTO> imageJPAToImageDTO;

    DefaultImageService(ImageRepository imageRepository,
                        VersionService versionService,
                        LanguageService languageService,
                        ImageHistoryService imageHistoryService, TernaryFunction<ImageDTO, Version, Language, ImageJPA> imageDTOToImageJPA,
                        ImageInTextFactory imageInTextFactory,
                        Function<ImageJPA, ImageDTO> imageJPAToImageDTO) {

        super(imageRepository);
        this.versionService = versionService;
        this.languageService = languageService;
        this.imageHistoryService = imageHistoryService;
        this.imageDTOToImageJPA = imageDTOToImageJPA;
        this.imageInTextFactory = imageInTextFactory;
        this.imageJPAToImageDTO = imageJPAToImageDTO;
    }

    @Override
    public ImageDTO getImage(ImageDTO dataHolder) {

        if ((dataHolder.getIndex() == null) && dataHolder.isInText()) {
            return imageInTextFactory.createWithIndex(dataHolder);
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
    public List<ImageJPA> getUsedImagesInWorkingAndLatestVersions(String imageURL) {
        List<ImageJPA> plainImageFound = repository.findByUrl(imageURL);

        Map<Integer, Optional<Integer>> imageMaxVersions = plainImageFound.stream()
                .map(ImageJPA::getVersion)
                .filter(image -> image.getNo() > 0)
                .collect(groupingBy(Version::getDocId,
                        mapping(Version::getNo, maxBy(Integer::compare))));

        List<Integer> latestDocIds = imageMaxVersions.keySet().stream()
                .filter(docId -> versionService.getLatestVersion(docId).getNo() == imageMaxVersions.get(docId).get())
                .collect(toList());

        imageMaxVersions.keySet().retainAll(latestDocIds);

        return plainImageFound.stream()
                .filter(image -> {
                    @NotNull final Version version = image.getVersion();
                    return version.getNo() == 0
                            || version.getNo() == (imageMaxVersions.getOrDefault(version.getDocId(), Optional.of(0)).get());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageJPA> getByDocId(Integer docId) {
        boolean isNewVersion = versionService.hasNewerVersion(docId);

        final Version version = isNewVersion
                ? versionService.getDocumentWorkingVersion(docId)
                : versionService.getLatestVersion(docId);


        return repository.findByVersion(version);
    }

    @Override
    public Set<ImageJPA> getImagesAllVersionAndLanguages(int docId, Language language) {
        final Version version = versionService.getLatestVersion(docId);
        return repository.findByVersionAndLanguage(version, new LanguageJPA(language));
    }

    @Override
    public void saveImage(ImageDTO imageDTO) {
        final Integer docId = imageDTO.getDocId();
        final Version version = versionService.getDocumentWorkingVersion(docId);

        generateImage(imageDTO);

        if (imageDTO.isAllLanguages()) {
            languageService.getAvailableLanguages().forEach(language -> saveImage(imageDTO, new LanguageJPA(language), version));
        } else {
            final LanguageJPA language = new LanguageJPA(languageService.findByCode(imageDTO.getLangCode()));
            saveImage(imageDTO, language, version);
            updateImagesWithDifferentLangCode(imageDTO, version);

        }

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
    public void regenerateImages() { // If generated images was cleared before start up
        repository.findAllRegenerationCandidates()
                .forEach((img) -> {
                    final ImageDTO imageDTO = imageJPAToImageDTO.apply(img);
                    imageDTO.setSource(ImcmsImageUtils.getImageSource(imageDTO.getPath()));

                    if (StringUtils.isBlank(imageDTO.getGeneratedFilename())) {
                        final String generatedFilename = ImcmsImageUtils.generateImageFileName(imageDTO);
                        imageDTO.setGeneratedFilename(generatedFilename);

                        img.setGeneratedFilename(generatedFilename);
                        repository.save(img);
                    }

                    ImcmsImageUtils.generateImage(imageDTO, false);
                });
    }

    @Override
    public void deleteImage(ImageDTO imageDTO) {
        final Integer docId = imageDTO.getDocId();
        final Version version = versionService.getDocumentWorkingVersion(docId);
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(imageDTO.getLangCode()));

        final Integer imageId = getImageId(imageDTO, version, language);

        if (imageId != null) {

            if (imageDTO.isAllLanguages()) {
                final LoopEntryRefDTO loopEntryRef = imageDTO.getLoopEntryRef();
                final Integer index = imageDTO.getIndex();

                languageService.getAvailableLanguages()
                        .stream()
                        .map(LanguageJPA::new)
                        .forEach(languageJPA -> {
                            final ImageJPA image = getImage(index, version, languageJPA, loopEntryRef);
                            repository.delete(image);
                        });
            } else {
                final ImageJPA image = imageDTOToImageJPA.apply(imageDTO, version, language);
                image.setId(imageId);

                repository.delete(image);

                updateImagesWithDifferentLangCode(imageDTO, version);
            }

            super.updateWorkingVersion(docId);
        }
    }

    private void updateImagesWithDifferentLangCode(ImageDTO imageDTO, Version version) {
        languageService.getAvailableLanguages().forEach(language -> {
            final LanguageJPA languageJPA = new LanguageJPA(language);
            final ImageJPA image = getImage(imageDTO.getIndex(), version, languageJPA, imageDTO.getLoopEntryRef());

            if (image != null) {
                image.setAllLanguages(false);
                repository.save(image);
            }
        });
    }

    private ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef,
                              Function<Integer, Version> versionReceiver) {

        final Version version = versionReceiver.apply(docId);

        final LanguageJPA language = new LanguageJPA(languageService.findByCode(langCode));
        final ImageJPA image = getImage(index, version, language, loopEntryRef);

        return Optional.ofNullable(image)
                .map(imageJPAToImageDTO)
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

    private ImageJPA getImage(int index, Version version, LanguageJPA language, LoopEntryRef loopEntryRef) {
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
        final ImageJPA image = getImage(index, version, language, loopEntryRefDTO);

        if (image == null) {
            return null;
        }

        return image.getId();
    }

    private void saveImage(ImageDTO imageDTO, LanguageJPA language, Version version) {
        final ImageJPA image = imageDTOToImageJPA.apply(imageDTO, version, language);
        final Integer imageId = getImageId(imageDTO, version, language);

        image.setId(imageId);
        repository.save(image);
        imageHistoryService.save(image);
    }

    @Override
    protected ImageJPA removeId(ImageJPA image, Version version) {
        return new ImageJPA(image, version);
    }
}
