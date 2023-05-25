package com.imcode.imcms.config;

import com.drew.imaging.ImageProcessingException;
import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.component.ImportToLocalCategoryResolver;
import com.imcode.imcms.domain.component.ImportToLocalRolePermissionResolver;
import com.imcode.imcms.domain.component.ImportToLocalTextDocumentTemplateResolver;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.factory.CommonContentFactory;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.LanguageMapper;
import imcode.server.document.textdocument.ImageSource;
import imcode.util.DateConstants;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static com.imcode.imcms.persistence.entity.Meta.PublicationStatus.APPROVED;

/**
 * Configuration class for mapping DTO -> JPA and vice versa, but not only.
 */
@Configuration
@Slf4j
class MappingConfig {

    @Bean
    public Function<MenuItem, MenuItemDTO> menuItemToDTO(DocumentMenuService documentMenuService) {
        return documentMenuService::getMenuItemDTO;
    }

    @Bean
    public UnaryOperator<MenuItem> toMenuItemsWithoutId() {
        return menuItem -> {
            final MenuItem newMenuItem = new MenuItem();
            newMenuItem.setDocumentId(menuItem.getDocumentId());
            newMenuItem.setSortOrder(menuItem.getSortOrder());

            return newMenuItem;
        };
    }

    @Bean
    public BiFunction<MenuItem, Language, MenuItemDTO> menuItemToMenuItemDtoWithLang(
            CommonContentService commonContentService,
            Function<MenuItem, MenuItemDTO> menuItemToDTO,
            LanguageService languageService,
            VersionService versionService,
            DocumentMenuService documentMenuService
    ) {
        return (menuItem, language) -> {

            final Integer docId = menuItem.getDocumentId();
            final Version latestVersion = versionService.getLatestVersion(docId);
            final List<CommonContent> enabledCommonContents = commonContentService.getByVersion(latestVersion)
                    .stream()
                    .filter(CommonContent::isEnabled)
                    .collect(Collectors.toList());

            if (enabledCommonContents.size() == 0) {
                return null;
            }

            final boolean isShowInDefaultLanguage = SHOW_IN_DEFAULT_LANGUAGE.equals(
                    documentMenuService.getDisabledLanguageShowMode(docId)
            );

            final Language defaultLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage();

            if (!isLanguageEnabled(enabledCommonContents, language) &&
                    (!isShowInDefaultLanguage || !isLanguageEnabled(enabledCommonContents, defaultLanguage))) {
                return null;
            }

            return menuItemToDTO.apply(menuItem);
        };
    }

    private boolean isLanguageEnabled(List<CommonContent> commonContents, Language language) {
        return commonContents.stream()
                .anyMatch(commonContent -> commonContent.getLanguage().getCode().equals(language.getCode()));
    }

    @Bean
    public Function<Menu, MenuDTO> menuToMenuDTO(Function<MenuItem, MenuItemDTO> menuItemToDTO) {
        return menu -> {
            final MenuDTO menuDTO = new MenuDTO();
            menuDTO.setDocId(menu.getVersion().getDocId());
            menuDTO.setMenuIndex(menu.getNo());
            menuDTO.setTypeSort(menu.getTypeSort());
            menuDTO.setMenuItems(menu.getMenuItems()
                    .stream()
                    .map(menuItemToDTO).collect(Collectors.toList()));

            return menuDTO;
        };
    }

    @Bean
    public Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem() {
        return new Function<MenuItemDTO, MenuItem>() {
            @Override
            public MenuItem apply(MenuItemDTO menuItemDTO) {
                final MenuItem menuItem = new MenuItem();
                menuItem.setDocumentId(menuItemDTO.getDocumentId());
                menuItem.setSortOrder(menuItemDTO.getSortOrder());
                menuItemDtoListToMenuItemList(this);
                return menuItem;
            }
        };
    }

    @Bean
    public Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList(Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem) {
        return menuItemDtoList -> menuItemDtoList.stream().map(menuItemDtoToMenuItem)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Bean
    public Function<ImageJPA, ImageDTO> imageJPAToImageDTO() {
        return image -> {
            final ImageDTO dto = new ImageDTO();

            dto.setIndex(image.getIndex());
            dto.setName(image.getName());
            dto.setDocId(image.getVersion().getDocId());
            dto.setLangCode(image.getLanguage().getCode());
            dto.setPath(image.getUrl());
            dto.setAllLanguages(image.isAllLanguages());

            final String generatedFilename = image.getGeneratedFilename();
            dto.setGeneratedFilename(generatedFilename);

            final String relativeGeneratedFilePath = StringUtils.isNotBlank(generatedFilename) ?
                    StoragePath.get(FILE, ImcmsConstants.IMAGE_GENERATED_FOLDER, generatedFilename).toString() : "";
            dto.setGeneratedFilePath(relativeGeneratedFilePath);

            final ImageSource imageSource = ImcmsImageUtils.getImageSource(image.getUrl());
            dto.setSource(imageSource);
            dto.setExifInfo(ImcmsImageUtils.getExifInfo(imageSource));
            dto.setSizeFormatted(formatFileSize(ImcmsImageUtils.getSize(imageSource)));

            dto.setFormat(image.getFormat());
            dto.setHeight(image.getHeight());
            dto.setWidth(image.getWidth());
            Optional.ofNullable(image.getLoopEntryRef()).map(LoopEntryRefDTO::new).ifPresent(dto::setLoopEntryRef);
            dto.setCropRegion(new ImageCropRegionDTO(image.getCropRegion()));
            dto.setInText(image.isInText());
            dto.setAlternateText(StringUtils.defaultIfEmpty(image.getAlternateText().trim(), " "));
            dto.setLinkUrl(image.getLinkUrl());
            dto.setBorder(image.getBorder());
            dto.setAlign(image.getAlign());
            dto.setLowResolutionUrl(image.getLowResolutionUrl());
            dto.setSpaceAround(image.getSpaceAround());
            dto.setTarget(image.getTarget());
            dto.setType(image.getType());
            dto.setRotateAngle(image.getRotateAngle());
            dto.setRotateDirection(ImageData.RotateDirection.fromAngle(image.getRotateAngle()));
            dto.setArchiveImageId(image.getArchiveImageId());
            dto.setResize(Resize.getByOrdinal(image.getResize()));

            return dto;
        };
    }

    @Bean
    public Function<ImageHistoryJPA, ImageHistoryDTO> imageHistoryJPAToImageHistoryDTO() {
        return image -> {
            final ImageHistoryDTO dto = new ImageHistoryDTO();

            dto.setIndex(image.getIndex());
            dto.setName(image.getName());
            dto.setDocId(image.getVersion().getDocId());
            dto.setLangCode(image.getLanguage().getCode());
            dto.setPath(image.getUrl());
            dto.setAllLanguages(image.isAllLanguages());

            final String generatedFilename = image.getGeneratedFilename();
            dto.setGeneratedFilename(generatedFilename);

            final String relativeGeneratedFilePath = StringUtils.isNotBlank(generatedFilename) ?
                    StoragePath.get(FILE, ImcmsConstants.IMAGE_GENERATED_FOLDER, generatedFilename).toString() : "";
            dto.setGeneratedFilePath(relativeGeneratedFilePath);

            final ImageSource imageSource = ImcmsImageUtils.getImageSource(image.getUrl());
            dto.setSource(imageSource);
            dto.setExifInfo(ImcmsImageUtils.getExifInfo(imageSource));
            dto.setSizeFormatted(formatFileSize(ImcmsImageUtils.getSize(imageSource)));

            dto.setFormat(image.getFormat());
            dto.setHeight(image.getHeight());
            dto.setWidth(image.getWidth());
            Optional.ofNullable(image.getLoopEntryRef()).map(LoopEntryRefDTO::new).ifPresent(dto::setLoopEntryRef);
            dto.setCropRegion(new ImageCropRegionDTO(image.getCropRegion()));
            dto.setInText(image.isInText());
            dto.setAlternateText(StringUtils.defaultIfEmpty(image.getAlternateText().trim(), " "));
            dto.setLinkUrl(image.getLinkUrl());
            dto.setBorder(image.getBorder());
            dto.setAlign(image.getAlign());
            dto.setLowResolutionUrl(image.getLowResolutionUrl());
            dto.setSpaceAround(image.getSpaceAround());
            dto.setTarget(image.getTarget());
            dto.setType(image.getType());
            dto.setRotateAngle(image.getRotateAngle());
            dto.setRotateDirection(ImageData.RotateDirection.fromAngle(image.getRotateAngle()));
            dto.setArchiveImageId(image.getArchiveImageId());
            dto.setResize(Resize.getByOrdinal(image.getResize()));
            dto.setModifiedAt(image.getModifiedAt());
            dto.setModifiedBy(image.getModifiedBy());

            return dto;
        };
    }

    @Bean
    public TernaryFunction<ImageDTO, Version, Language, ImageJPA> imageDTOToImageJPA() {

        return (imageDTO, version, language) -> {
            final ImageJPA image = new ImageJPA();

            final String path = imageDTO.getPath();
            image.setUrl(StringUtils.removeStart(path, "/"));

            image.setIndex(imageDTO.getIndex());
            image.setVersion(version);
            image.setLanguage(new LanguageJPA(language));
            image.setHeight(imageDTO.getHeight());
            image.setWidth(imageDTO.getWidth());
            image.setName(imageDTO.getName());
            image.setGeneratedFilename(imageDTO.getGeneratedFilename());
            Optional.ofNullable(imageDTO.getLoopEntryRef()).map(LoopEntryRefJPA::new).ifPresent(image::setLoopEntryRef);
            image.setFormat(imageDTO.getFormat());
            image.setCropRegion(new ImageCropRegionJPA(imageDTO.getCropRegion()));
            image.setAllLanguages(imageDTO.isAllLanguages());
            image.setInText(imageDTO.isInText());
            image.setAlternateText(StringUtils.defaultIfEmpty(imageDTO.getAlternateText().trim(), " "));
            image.setLinkUrl(imageDTO.getLinkUrl());
            image.setBorder(imageDTO.getBorder());
            image.setAlign(imageDTO.getAlign());
            image.setLowResolutionUrl(imageDTO.getLowResolutionUrl());
            image.setSpaceAround(imageDTO.getSpaceAround());
            image.setTarget(imageDTO.getTarget());
            image.setType(imageDTO.getName().isEmpty() ? imageDTO.getType() : 0);
            image.setRotateAngle(imageDTO.getRotateDirection().toAngle());
            image.setArchiveImageId(imageDTO.getArchiveImageId());
            image.setResize(imageDTO.getResize() == null ? 0 : imageDTO.getResize().getOrdinal());
            image.setCompress(imageDTO.isCompress());

            return image;
        };
    }

    @Bean
    public Function<DocumentDTO, Meta> documentDtoToMeta(CategoryService categoryService, VersionService versionService) {
        return documentDTO -> {
            final Meta meta = new Meta();
            final Integer id = documentDTO.getId();
            final int version = id != null ? versionService.getLatestVersion(id).getNo() : Version.WORKING_VERSION_INDEX;

            meta.setId(id);
            meta.setDefaultVersionNo(version);
	        meta.setDefaultLanguageAliasEnabled(documentDTO.isDefaultLanguageAliasEnabled());
	        meta.setPublicationStatus(documentDTO.getPublicationStatus());
            meta.setTarget(documentDTO.getTarget());
            meta.setDocumentType(documentDTO.getType());
            meta.setKeywords(documentDTO.getKeywords());

            // todo: check publication status and save here
	        final int currentUserId = Imcms.getUser().getId();
	        final Date currentDateTime = new Date();

	        // save creator to version too
	        final AuditDTO creation = documentDTO.getCreated();
	        final Integer creatorId = Optional.ofNullable(creation.getId()).orElse(currentUserId);
	        meta.setCreatorId(creatorId);

	        final Date createdDatetime = ((creation.getDate() == null) || (creation.getTime() == null))
			        ? currentDateTime : creation.getFormattedDate();
	        meta.setCreatedDatetime(createdDatetime);

	        meta.setModifierId(currentUserId);
	        meta.setModifiedDatetime(currentDateTime);

	        final AuditDTO archivationDto = documentDTO.getArchived();
	        final Date archivationDate = archivationDto.getFormattedDate();
	        meta.setArchiverId(archivationDate == null ? null : currentUserId);
	        meta.setArchivedDatetime(archivationDate);

	        final AuditDTO publicationDto = documentDTO.getPublished();
	        final Date publicationDate = publicationDto.getFormattedDate();
	        meta.setPublisherId(publicationDate == null ? null : currentUserId);
	        meta.setPublicationStartDatetime(publicationDate);

	        final AuditDTO publicationEndDto = documentDTO.getPublicationEnd();
	        final Date publicationEndDate = publicationEndDto.getFormattedDate();
	        meta.setDepublisherId(publicationEndDate == null ? null : currentUserId);
	        meta.setPublicationEndDatetime(publicationEndDate);

	        meta.setProperties(documentDTO.getProperties());
	        meta.setDisabledLanguageShowMode(documentDTO.getDisabledLanguageShowMode());
	        meta.setSearchDisabled(documentDTO.isSearchDisabled());

	        final Set<Category> categories = documentDTO.getCategories()
			        .stream()
			        .map(categoryDTO -> categoryService.getById(categoryDTO.getId()).map(CategoryJPA::new))
			        .filter(Optional::isPresent)
			        .map(Optional::get)
			        .collect(Collectors.toSet());

            meta.setCategories(categories);

            meta.setLinkableByOtherUsers(documentDTO.isLinkableByOtherUsers());
            meta.setLinkedForUnauthorizedUsers(documentDTO.isLinkableForUnauthorizedUsers());

            meta.setCacheForUnauthorizedUsers(documentDTO.isCacheForUnauthorizedUsers());
            meta.setCacheForAuthorizedUsers(documentDTO.isCacheForAuthorizedUsers());

            meta.setVisible(documentDTO.isVisible());
			meta.setImported(documentDTO.isImported());

            meta.setRoleIdToPermission(documentDTO.getRoleIdToPermission());

            final Set<RestrictedPermissionJPA> restrictedPermissions = documentDTO.getRestrictedPermissions()
                    .stream()
                    .map(RestrictedPermissionJPA::new)
                    .collect(Collectors.toSet());

            meta.setRestrictedPermissions(restrictedPermissions);
            meta.setDocumentWasteBasket(documentDTO.getDocumentWasteBasket());

            return meta;
        };
    }

    @Bean
    public BiFunction<Meta, List<CommonContent>, DocumentDTO> documentMapping(UserService userService, VersionService versionService) {
        final BiFunction<Supplier<Integer>, Supplier<Date>, AuditDTO> auditDtoCreator =
                (auditorIdSupplier, auditedDateSupplier) -> {

                    final AuditDTO audit = new AuditDTO();
                    final Integer userId = auditorIdSupplier.get();
                    audit.setDateTime(auditedDateSupplier.get());

                    if (userId != null) {
                        audit.setId(userId);
                        audit.setBy(userService.getUser(userId).getLogin());
                    }

                    return audit;
                };

        return (meta, commonContents) -> {
            final DocumentDTO dto = new DocumentDTO();
            final Integer metaId = meta.getId();
            dto.setId(metaId);
	        dto.setTarget(meta.getTarget());
	        dto.setDefaultLanguageAliasEnabled(meta.isDefaultLanguageAliasEnabled());
	        dto.setPublicationStatus(meta.getPublicationStatus());
            dto.setCommonContents(commonContents);
            dto.setPublished(auditDtoCreator.apply(meta::getPublisherId, meta::getPublicationStartDatetime));
            dto.setPublicationEnd(auditDtoCreator.apply(meta::getDepublisherId, meta::getPublicationEndDatetime));
            dto.setArchived(auditDtoCreator.apply(meta::getArchiverId, meta::getArchivedDatetime));
            dto.setCreated(auditDtoCreator.apply(meta::getCreatorId, meta::getCreatedDatetime));
            dto.setModified(auditDtoCreator.apply(meta::getModifierId, meta::getModifiedDatetime));
            dto.setDisabledLanguageShowMode(meta.getDisabledLanguageShowMode());

            Version currentVersion = versionService.getCurrentVersion(metaId);
            dto.setCurrentVersion(new AuditDTO(currentVersion.getNo(), currentVersion.getModifiedBy().getLogin(), currentVersion.getModifiedDt()));

            Version latestVersion = versionService.getLatestVersion(metaId);
            dto.setLatestVersion(new AuditDTO(latestVersion.getNo(), latestVersion.getCreatedBy().getLogin(), latestVersion.getCreatedDt()));

            dto.setSearchDisabled(meta.isSearchDisabled());
            dto.setKeywords(meta.getKeywords());
            dto.setRoleIdToPermission(meta.getRoleIdToPermission());
            dto.setCategories(meta.getCategories());
            dto.setRestrictedPermissions(new HashSet<>(meta.getRestrictedPermissions()));
            dto.setProperties(meta.getProperties());
            dto.setType(meta.getDocumentType());
            dto.setLinkableByOtherUsers(meta.getLinkableByOtherUsers());
            dto.setLinkableForUnauthorizedUsers(meta.getLinkedForUnauthorizedUsers());
            dto.setCacheForUnauthorizedUsers(meta.isCacheForUnauthorizedUsers());
            dto.setCacheForAuthorizedUsers(meta.isCacheForAuthorizedUsers());
            dto.setVisible(meta.getVisible());
			dto.setImported(meta.isImported());
            dto.setDocumentWasteBasket(meta.getDocumentWasteBasket());

            return dto;
        };
    }

    @Bean
    public Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO(@Qualifier("imageStorageClient") StorageClient storageClient,
                                                                         @Value("${ImagePath}") String imagesPath) {
        final StoragePath imagesStoragePath = StoragePath.get(DIRECTORY, imagesPath);

        return storagePath -> {
            final ImageFileDTO imageFileDTO = new ImageFileDTO();

            try (final StorageFile imageFile = storageClient.getFile(storagePath)) {

                final String fileName = storagePath.getName();
                imageFileDTO.setName(fileName);

                imageFileDTO.setFormat(Format.findFormat(FilenameUtils.getExtension(fileName)));
                imageFileDTO.setPath(imagesStoragePath.relativize(storagePath).toString());

                final Date lastModifiedDate = new Date(imageFile.lastModified());
                final String formattedDate = DateConstants.DATETIME_DOC_FORMAT.format(lastModifiedDate);
                imageFileDTO.setUploaded(formattedDate);

                imageFileDTO.setSize(formatFileSize(imageFile.size()));

                final Dimension imageDimension = ImcmsImageUtils.getImageDimension(imageFile.getContent());
                if (imageDimension != null) {
                    imageFileDTO.setWidth(imageDimension.width);
                    imageFileDTO.setHeight(imageDimension.height);
                    imageFileDTO.setResolution(imageDimension.width + "x" + imageDimension.height);
                }

                imageFileDTO.setExifInfo(ImcmsImageUtils.getExifInfo(imageFile.getContent()));
            }catch (StorageFileNotFoundException e){
                log.error("Exception while mapping StoragePath to ImageFileDTO", e);
            }catch (ImageProcessingException | IOException e){
                log.error("Exception while getting exif info from storage file", e);
            }

            return imageFileDTO;
        };
    }

    private String formatFileSize(long fileSize) {
        final String suffix;

        final long k = 1000L;
        final long square = k * k;
        final long cube = square * k;

        if (fileSize >= cube) {
            suffix = "GB"; // I hope it's not the real case
            fileSize /= cube;

        } else if (fileSize >= square) {
            suffix = "MB";
            fileSize /= square;

        } else if (fileSize >= k) {
            suffix = "kB";
            fileSize /= k;

        } else {
            suffix = "B";
        }

        return fileSize + suffix;
    }

    @Bean
    public BiFunction<StoragePath, Boolean, ImageFolderDTO> storagePathToImageFolderDTO(Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO,
                                                                                      @Qualifier("imageStorageClient") StorageClient storageClient,
                                                                                      @Value("${ImagePath}") String imagesPath) {
        final StoragePath imagesStoragePath = StoragePath.get(DIRECTORY, imagesPath);
        final StoragePath generatedImagesPath = imagesStoragePath.resolve(DIRECTORY, ImcmsConstants.IMAGE_GENERATED_FOLDER);

        return new BiFunction<StoragePath, Boolean, ImageFolderDTO>() {
            @Override
            public ImageFolderDTO apply(StoragePath folderPath, Boolean isRoot) {
                final ImageFolderDTO imageFolderDTO = new ImageFolderDTO();
                imageFolderDTO.setName(folderPath.getName());

                final String relativePath = imagesStoragePath.relativize(folderPath).toString();
                imageFolderDTO.setPath(relativePath);

                final ArrayList<ImageFolderDTO> subFolders = new ArrayList<>();
                final ArrayList<ImageFileDTO> folderFiles = new ArrayList<>();

                final List<StoragePath> filesPath = storageClient.listPaths(folderPath).stream()
                        .sorted(Comparator.comparing(StoragePath::getName))
                        .collect(Collectors.toList());

                for (StoragePath path : filesPath) {
                    if (path.getType() == DIRECTORY && !path.equals(generatedImagesPath)) {
                        subFolders.add(this.apply(path, false));
                    } else if (isRoot && Format.isImage(FilenameUtils.getExtension(path.getName()))) {
                        folderFiles.add(storagePathToImageFileDTO.apply(path));
                    }
                }

                imageFolderDTO.setFiles(folderFiles);
                imageFolderDTO.setFolders(subFolders);

                return imageFolderDTO;
            }
        };
    }

    @Bean
    public Function<TextHistoryJPA, TextHistoryDTO> textHistoryJpaToTextHistoryDTO(final UserService userService) {
        return textHistoryJPA -> {
            final User modifierUser = userService.getUser(textHistoryJPA.getModifierId());

            final TextHistoryDTO textHistoryDTO = new TextHistoryDTO(textHistoryJPA);
            textHistoryDTO.setLangCode(textHistoryJPA.getLangCode());
            textHistoryDTO.setModifiedBy(new UserDTO(modifierUser));

            return textHistoryDTO;
        };
    }

    @Bean
    public BiFunction<Path, Boolean, SourceFile> fileToSourceFile(@Value("${rootPath}") Path rootPath) {
        return (path, withContent) -> {
            final SourceFile.FileType fileType = Files.isDirectory(path) ? DIRECTORY : FILE;
            final String physicalPath = path.toAbsolutePath().toString().substring(rootPath.toString().length());
            byte[] contents = null;
            try {
                if (withContent) contents = Files.readAllBytes(path);
            } catch (IOException e) {
                log.info("File has not content!!!");
            }
            final String size = formatFileSize(path.toFile().length());

            return new SourceFile(path.getFileName().toString(), physicalPath, path.toString(), fileType, contents, size, true, 0);
        };
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

	@Bean
	public Function<ImportDocumentDTO, UberDocumentDTO> importDocumentToDocument(
			CommonContentFactory commonContentFactory,
			ImportToLocalTextDocumentTemplateResolver templateResolver,
			ImportToLocalCategoryResolver categoryResolver,
			ImportToLocalRolePermissionResolver rolePermissionResolver

	){
		return importDocument -> {
			final UberDocumentDTO document = new UberDocumentDTO();

			document.setType(Meta.DocumentType.getByRB4Name(importDocument.getType()));
			document.setFiles(Collections.emptyList());

			if (document.getType().equals(Meta.DocumentType.URL)) {
				document.setDocumentURL(DocumentUrlDTO.createDefaultWithUrl(importDocument.getUrl()));
			}

			if (document.getType().equals(Meta.DocumentType.TEXT)) {
				document.setTemplate(templateResolver.resolve(importDocument.getTemplate()));
			}

			document.setCategories(importDocument.getCategories().stream()
					.map(categoryResolver::resolve)
					.collect(Collectors.toSet()));

			document.setRoleIdToPermission(rolePermissionResolver.resolve(importDocument.getRoles()));

			document.setTarget(importDocument.getTarget());
			document.setKeywords(importDocument.getKeywords());
			document.setSearchDisabled(importDocument.isSearchDisabled());
			document.setLinkableForUnauthorizedUsers(importDocument.isLinkableForUnauthorizedUsers());
			document.setLinkableByOtherUsers(importDocument.isLinkableByOtherUsers());
			document.setTarget(importDocument.getTarget());

			document.setImported(true);
			document.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
			document.setPublicationStatus(APPROVED);

            final List<CommonContent> commonContents = commonContentFactory.createCommonContents();
            commonContents.stream()
                    .peek(commonContent -> {
                        commonContent.setAlias("");
                        commonContent.setHeadline("");
                        commonContent.setMenuText("");
                    })
                    .filter(commonContent -> commonContent.getLanguage().getCode().equals(LanguageMapper.convert639_2to639_1(importDocument.getDefaultLanguage())))
                    .forEach(commonContent -> {
                        final String customAlias = "import/" + importDocument.getId();

                        commonContent.setAlias(StringUtils.defaultIfBlank(importDocument.getAlias(), customAlias));
                        commonContent.setHeadline(StringUtils.defaultIfBlank(importDocument.getHeadline(),""));
                        commonContent.setMenuText(StringUtils.defaultIfBlank(importDocument.getMenuText(),""));
                    });

            document.setCommonContents(commonContents);
			document.setDefaultLanguageAliasEnabled(true);

			document.setProperties(importDocument.getProperties()
					.stream()
					.collect(Collectors.toMap(ImportPropertyDTO::getKey, ImportPropertyDTO::getValue))
			);

			final RestrictedPermissionDTO restricted1 = new RestrictedPermissionDTO();
			final RestrictedPermissionDTO restricted2 = new RestrictedPermissionDTO();
			restricted1.setPermission(Meta.Permission.RESTRICTED_1);
			restricted2.setPermission(Meta.Permission.RESTRICTED_2);
			document.setRestrictedPermissions(Set.of(restricted1, restricted2));

			final AuditDTO createdAt = new AuditDTO();
			createdAt.setDateTime(importDocument.getCreatedAt());
			if (importDocument.getCreatedAt() == null) {
				createdAt.setDateTime(new Date());
                createdAt.setBy(importDocument.getCreator());
			}
			document.setCreated(createdAt);

			final AuditDTO modifiedAt = new AuditDTO();
			modifiedAt.setDateTime(importDocument.getChangedAt());
			document.setModified(modifiedAt);

			final AuditDTO publishedAt = new AuditDTO();
			publishedAt.setDateTime(importDocument.getPublishedAt());
			document.setPublished(publishedAt);

			final AuditDTO publicationEnd = new AuditDTO();
			publicationEnd.setDateTime(importDocument.getExpiresAt());
			document.setPublicationEnd(publicationEnd);

			document.setArchived(new AuditDTO());

			document.setCurrentVersion(new AuditDTO());
			document.getCurrentVersion().setId(Version.WORKING_VERSION_INDEX);

			return document;
		};
	}

	@Bean
	public BiConsumer<ImportDocumentDTO, Document> updateFromImported(
			ImportToLocalCategoryResolver categoryResolver,
			ImportToLocalRolePermissionResolver rolePermissionResolver
	){
		return (importDocument, document)  -> {

			document.setCategories(importDocument.getCategories().stream()
					.map(categoryResolver::resolve)
					.collect(Collectors.toSet()));

			document.setRoleIdToPermission(rolePermissionResolver.resolve(importDocument.getRoles()));

			document.setTarget(importDocument.getTarget());
			document.setKeywords(importDocument.getKeywords());
			document.setSearchDisabled(importDocument.isSearchDisabled());
			document.setLinkableForUnauthorizedUsers(importDocument.isLinkableForUnauthorizedUsers());
			document.setLinkableByOtherUsers(importDocument.isLinkableByOtherUsers());
			document.setTarget(importDocument.getTarget());

			document.getCommonContents().stream()
					.filter(commonContent -> commonContent.getLanguage().getCode().equals(LanguageMapper.convert639_2to639_1(importDocument.getDefaultLanguage())))
					.forEach(commonContent -> {
						commonContent.setAlias(StringUtils.isBlank(importDocument.getAlias()) ? "import/" + importDocument.getId() : importDocument.getAlias());
						commonContent.setHeadline(importDocument.getHeadline());
						commonContent.setMenuText(importDocument.getMenuText());
					});

			document.setProperties(importDocument.getProperties()
					.stream()
					.collect(Collectors.toMap(ImportPropertyDTO::getKey, ImportPropertyDTO::getValue))
			);
		};
	}

}
