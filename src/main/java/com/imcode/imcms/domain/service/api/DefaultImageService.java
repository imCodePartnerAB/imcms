package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.component.ImageCacheManager;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.exception.ImageAlternateTextRequiredException;
import com.imcode.imcms.domain.factory.ImageInTextFactory;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.util.ImcmsImageUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static imcode.server.ImcmsConstants.OTHER_CACHE_NAME;
import static imcode.server.ImcmsConstants.PUBLIC_CACHE_NAME;
import static java.util.stream.Collectors.*;

@Log4j2
@Transactional
@Service("imageService")
class DefaultImageService extends AbstractVersionedContentService<ImageJPA, ImageRepository> implements ImageService {

    //@Cachable works by proxy class, only external method calls coming in through the proxy are intercepted.
    //In order to use the cache when we call a method inside own class, we need to use the autowired bean of this class.
    @Autowired
    private ImageService self;

    private final VersionService versionService;
    private final LanguageService languageService;
    private final ImageHistoryService imageHistoryService;
    private final ImageInTextFactory imageInTextFactory;

    private final TernaryFunction<ImageDTO, Version, Language, ImageJPA> imageDTOToImageJPA;
    private final Function<ImageJPA, ImageDTO> imageJPAToImageDTO;

    private final StorageClient storageClient;

    private final ImageCacheManager imageCacheManager;
    private final boolean isImageEditorAltTextRequired;

    DefaultImageService(ImageRepository imageRepository,
                        VersionService versionService,
                        LanguageService languageService,
                        ImageHistoryService imageHistoryService, TernaryFunction<ImageDTO, Version, Language, ImageJPA> imageDTOToImageJPA,
                        ImageInTextFactory imageInTextFactory,
                        Function<ImageJPA, ImageDTO> imageJPAToImageDTO,
                        @Qualifier("imageStorageClient") StorageClient storageClient,
                        ImageCacheManager imageCacheManager,
                        @Value("${image.editor.alt-text.required}") boolean isImageEditorAltTextRequired) {

        super(imageRepository);
        this.versionService = versionService;
        this.languageService = languageService;
        this.imageHistoryService = imageHistoryService;
        this.imageInTextFactory = imageInTextFactory;

        this.imageDTOToImageJPA = imageDTOToImageJPA;
        this.imageJPAToImageDTO = imageJPAToImageDTO;

        this.storageClient = storageClient;

        this.imageCacheManager = imageCacheManager;
        this.isImageEditorAltTextRequired = isImageEditorAltTextRequired;
    }

    @Override
    public ImageDTO getImage(ImageDTO dataHolder) {

        if ((dataHolder.getIndex() == null) && dataHolder.isInText()) {
            return imageInTextFactory.createWithIndex(dataHolder);
        }

        return self.getImage(
                dataHolder.getDocId(),
                dataHolder.getIndex(),
                dataHolder.getLangCode(),
                dataHolder.getLoopEntryRef()
        );
    }

    @Cacheable(cacheNames = OTHER_CACHE_NAME, key = "#docId+'-'+#langCode+'-'+#index+'-'+#loopEntryRef")
    @Override
    public ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getImage(docId, index, langCode, loopEntryRef, versionService.getDocumentWorkingVersion(docId));
    }

    @Override
    public ImageDTO getImage(int docId, int index, int versionNo, String langCode, LoopEntryRef loopEntryRef) {
        final ImageDTO imageDTO = getImage(docId, index, langCode, loopEntryRef, versionService.findByDocIdAndNo(docId, versionNo));

        regenerateImage(imageDTO);

        return imageDTO;
    }

    @Cacheable(cacheNames = PUBLIC_CACHE_NAME, key = "#docId+'-'+#langCode+'-'+#index+'-'+#loopEntryRef")
    @Override
    public ImageDTO getPublicImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getImage(docId, index, langCode, loopEntryRef, versionService.getLatestVersion(docId));
    }

    @Override
    public List<ImageDTO> getLoopImages(int docId, String langCode, int loopIndex) {
        final LanguageJPA languageJPA = new LanguageJPA(languageService.findByCode(langCode));

        return repository.findByVersionAndLanguageAndLoopIndex(
                        versionService.getDocumentWorkingVersion(docId), languageJPA, loopIndex)
                .parallelStream()
                .map(imageJPA ->    //use cache
                        self.getImage(docId, imageJPA.getIndex(), langCode, new LoopEntryRefDTO(imageJPA.getLoopEntryRef()))
                ).toList();
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
	public List<ImageDTO> getImagesByUrl(String url) {
		return repository.findByUrl(url).stream().map(imageJPAToImageDTO).collect(Collectors.toList());
	}

	@Override
	public List<ImageDTO> getImagesByFolderInUrl(String folder) {
		return repository.findByFolderInUrl(folder).stream().map(imageJPAToImageDTO).collect(Collectors.toList());
	}

	@CacheEvict(cacheNames = OTHER_CACHE_NAME, key = "#imageDTO.docId+'-'+#imageDTO.langCode+'-'+#imageDTO.index+'-'+#imageDTO.loopEntryRef")
    @Override
    public void saveImage(ImageDTO imageDTO) {
        if (isImageEditorAltTextRequired && StringUtils.isBlank(imageDTO.getAlternateText())){
            log.error("Failed to save image: alt text missing!");
            throw new ImageAlternateTextRequiredException();
        }

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
        imageCacheManager.removeOtherImagesFromCacheByKey(""+docId);

        super.updateWorkingVersion(docId);
        indexAndCacheActualizationAfterCommit(docId);
    }

    @CacheEvict(cacheNames = OTHER_CACHE_NAME, allEntries = true)
    @Override
    public void setAsWorkingVersion(Version version) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(version.getDocId());

        final List<ImageJPA> imagesByVersion = repository.findByVersion(version);

        final List<ImageJPA> saveImages = new ArrayList<>();
        imagesByVersion.forEach(imageByVersion -> {
            ImageJPA imageCopy = new ImageJPA(imageByVersion, workingVersion);
            imageCopy.setId(null);
            saveImages.add(imageCopy);
        });

        repository.deleteByVersion(workingVersion);
        repository.flush();
        repository.saveAll(saveImages);

        saveImages.forEach(image -> {
            regenerateImage(imageJPAToImageDTO.apply(image));
            imageHistoryService.save(image);
        });
    }

    @CacheEvict(cacheNames = OTHER_CACHE_NAME, allEntries = true)
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

                    if (StringUtils.isBlank(imageDTO.getGeneratedFilename())) {
                        final String generatedFilename = ImcmsImageUtils.generateImageFileName(imageDTO);
                        imageDTO.setGeneratedFilename(generatedFilename);

                        img.setGeneratedFilename(generatedFilename);
                        repository.save(img);
                    }

                    regenerateImage(imageDTO);
                });
    }

    @Override
    public void regenerateImage(ImageDTO imageDTO){
        final StoragePath path = StoragePath.get(SourceFile.FileType.FILE,
                ImcmsImageUtils.imagesPath,
                ImcmsConstants.IMAGE_GENERATED_FOLDER,
                imageDTO.getGeneratedFilename());
        if(!storageClient.exists(path)){
            byte[] content = ImcmsImageUtils.generateImage(imageDTO);
            saveGeneratedImageFile(imageDTO.getGeneratedFilename(), content);
        }
    }

	@CacheEvict(cacheNames = OTHER_CACHE_NAME, key = "#imageDTO.docId+'-'+#imageDTO.langCode+'-'+#imageDTO.index+'-'+#imageDTO.loopEntryRef")
	@Override
	public void updateImage(ImageDTO imageDTO) {
		final Integer docId = imageDTO.getDocId();
		final Version version = versionService.getDocumentWorkingVersion(docId);
		final LanguageJPA language = new LanguageJPA(languageService.findByCode(imageDTO.getLangCode()));

		saveImage(imageDTO, language, version);
	}

	@CacheEvict(cacheNames = OTHER_CACHE_NAME, key = "#imageDTO.docId+'-'+#imageDTO.langCode+'-'+#imageDTO.index+'-'+#imageDTO.loopEntryRef")
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
            indexAndCacheActualizationAfterCommit(docId);
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

    private ImageDTO getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef, Version version) {
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

            byte[] content = ImcmsImageUtils.generateImage(imageDTO);
            saveGeneratedImageFile(imageDTO.getGeneratedFilename(), content);
        }
    }

    private void saveGeneratedImageFile(String fileName, byte[] content){
        if(content == null) return;

        StoragePath path = StoragePath.get(SourceFile.FileType.FILE,
                ImcmsImageUtils.imagesPath,
                ImcmsConstants.IMAGE_GENERATED_FOLDER,
                fileName);
        storageClient.put(path, new ByteArrayInputStream(content));
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

    private void indexAndCacheActualizationAfterCommit(int docId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
                indexAndCacheActualization(docId);
                if (!Imcms.isVersioningAllowed()) {
                    imageCacheManager.removePublicImagesFromCacheByKey("" + docId);
                }
            }
        });
    }
}
