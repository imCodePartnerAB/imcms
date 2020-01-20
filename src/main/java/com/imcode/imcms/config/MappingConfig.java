package com.imcode.imcms.config;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.mapping.jpa.doc.Property;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static imcode.server.document.DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS;
import static ucar.httpservices.HTTPAuthStore.log;

/**
 * Configuration class for mapping DTO -> JPA and vice versa, but not only.
 */
@Configuration
class MappingConfig {

    @Bean
    public BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDTO(DocumentMenuService documentMenuService) {
        return new BiFunction<MenuItem, Language, MenuItemDTO>() {
            @Override
            public MenuItemDTO apply(MenuItem menuItem, Language language) {
                final MenuItemDTO menuItemDTO = documentMenuService.getMenuItemDTO(menuItem.getDocumentId(), language);

                final List<MenuItemDTO> children = menuItem.getChildren()
                        .stream()
                        .map(menuItemChild -> this.apply(menuItemChild, language))
                        .collect(Collectors.toList());

                menuItemDTO.setChildren(children);

                return menuItemDTO;
            }
        };
    }

    @Bean
    public UnaryOperator<MenuItem> toMenuItemsWithoutId() {
        return new UnaryOperator<MenuItem>() {
            @Override
            public MenuItem apply(MenuItem menuItem) {
                final MenuItem newMenuItem = new MenuItem();
                newMenuItem.setDocumentId(menuItem.getDocumentId());
                newMenuItem.setSortOrder(menuItem.getSortOrder());

                final Set<MenuItem> newChildren = menuItem.getChildren()
                        .stream()
                        .map(this)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                newMenuItem.setChildren(newChildren);

                return newMenuItem;
            }
        };
    }

    @Bean
    public BiFunction<MenuItem, Language, MenuItemDTO> menuItemToMenuItemDtoWithLang(
            CommonContentService commonContentService,
            BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDTO,
            LanguageService languageService,
            VersionService versionService,
            DocumentMenuService documentMenuService
    ) {
        return new BiFunction<MenuItem, Language, MenuItemDTO>() {

            @Override
            public MenuItemDTO apply(final MenuItem menuItem, final Language language) {

                final Integer docId = menuItem.getDocumentId();
                final Version latestVersion = versionService.getLatestVersion(docId);
                final List<CommonContent> enabledCommonContents = commonContentService.getByVersion(latestVersion)
                        .stream()
                        .filter(CommonContent::isEnabled)
                        .collect(Collectors.toList());

                if (enabledCommonContents.size() == 0) {
                    return null;
                }

                final boolean isLanguageDisabled = enabledCommonContents.stream()
                        .noneMatch(commonContent -> commonContent.getLanguage().getCode().equals(language.getCode()));

                final Language menuLanguage;

                if (isLanguageDisabled) {

                    final boolean isShowInDefaultLanguage = SHOW_IN_DEFAULT_LANGUAGE.equals(
                            documentMenuService.getDisabledLanguageShowMode(docId)
                    );

                    if (isShowInDefaultLanguage) {
                        menuLanguage = languageService.findByCode(Imcms.getUser().getLanguage());

                    } else {
                        return null;
                    }
                } else {
                    menuLanguage = language;
                }

                final MenuItemDTO menuItemDTO = menuItemToDTO.apply(menuItem, menuLanguage);

                final List<MenuItemDTO> children = menuItem.getChildren()
                        .stream()
                        .map(menuItem1 -> this.apply(menuItem1, language))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                menuItemDTO.setChildren(children);

                return menuItemDTO;
            }
        };
    }

    @Bean
    public BiFunction<Menu, Language, MenuDTO> menuToMenuDTO(BiFunction<MenuItem, Language, MenuItemDTO> menuItemToDTO) {
        return (menu, language) -> {
            final MenuDTO menuDTO = new MenuDTO();
            menuDTO.setDocId(menu.getVersion().getDocId());
            menuDTO.setMenuIndex(menu.getNo());
            menuDTO.setNested(menu.isNested());
            menuDTO.setTypeSort(menu.getTypeSort());
            menuDTO.setMenuItems(menu.getMenuItems()
                    .stream()
                    .map(menuItem -> menuItemToDTO.apply(menuItem, language)).collect(Collectors.toList()));

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
                final Set<MenuItem> children = menuItemDtoListToMenuItemList(this).apply(menuItemDTO.getChildren());
                menuItem.setChildren(children);
                return menuItem;
            }
        };
    }

    @Bean
    public Function<List<MenuItemDTO>, Set<MenuItem>> menuItemDtoListToMenuItemList(Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem) {
        return menuItemDtoList -> IntStream.range(0, menuItemDtoList.size())
                .mapToObj(i -> {
                    final MenuItem menuItem = menuItemDtoToMenuItem.apply(menuItemDtoList.get(i));
                    menuItem.setSortOrder(i + 1);
                    return menuItem;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Bean
    public Function<ImageJPA, ImageDTO> imageJPAToImageDTO(@Value("${ImageUrl}") String imagesPath) {
        final String generatedImagesPath = imagesPath + ImcmsConstants.IMAGE_GENERATED_FOLDER + org.apache.hadoop.fs.Path.SEPARATOR;
        // Path.SEPARATOR slashes use in everywhere, for different OS

        return image -> {
            final ImageDTO dto = new ImageDTO();

            dto.setIndex(image.getIndex());
            dto.setName(image.getName());
            dto.setDocId(image.getVersion().getDocId());
            dto.setLangCode(image.getLanguage().getCode());
            dto.setPath(image.getUrl());
            dto.setAllLanguages(image.isAllLanguages());

            dto.setExifInfo(ImcmsImageUtils.getExifInfo(image.getUrl()));

            final boolean filenameExists = (image.getGeneratedFilename() != null)
                    && !image.getGeneratedFilename().equals("");

            final String generatedFilePath = filenameExists ? (generatedImagesPath + image.getGeneratedFilename()) : "";

            dto.setGeneratedFilePath(generatedFilePath);
            dto.setGeneratedFilename(image.getGeneratedFilename());
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
    public Function<ImageHistoryJPA, ImageHistoryDTO> imageHistoryJPAToImageHistoryDTO(@Value("${ImageUrl}") String imagesPath) {
        final String generatedImagesPath = imagesPath + ImcmsConstants.IMAGE_GENERATED_FOLDER + org.apache.hadoop.fs.Path.SEPARATOR;
        // Path.SEPARATOR slashes use in everywhere, for different OS

        return image -> {
            final ImageHistoryDTO dto = new ImageHistoryDTO();

            dto.setIndex(image.getIndex());
            dto.setName(image.getName());
            dto.setDocId(image.getVersion().getDocId());
            dto.setLangCode(image.getLanguage().getCode());
            dto.setPath(image.getUrl());
            dto.setAllLanguages(image.isAllLanguages());

            dto.setExifInfo(ImcmsImageUtils.getExifInfo(image.getUrl()));

            final boolean filenameExists = (image.getGeneratedFilename() != null)
                    && !image.getGeneratedFilename().equals("");

            final String generatedFilePath = filenameExists ? (generatedImagesPath + image.getGeneratedFilename()) : "";

            dto.setGeneratedFilePath(generatedFilePath);
            dto.setGeneratedFilename(image.getGeneratedFilename());
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
            image.setUrl(!path.isEmpty() && path.startsWith("/") ? path.substring(1) : path);

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

            return image;
        };
    }

    @Bean
    public Function<DocumentDTO, Meta> documentDtoToMeta(CategoryService categoryService, PropertyRepository propertyRepository) {
        return documentDTO -> {
            final Meta meta = new Meta();
            final Integer version = documentDTO.getCurrentVersion().getId();

            meta.setId(documentDTO.getId());
            meta.setDefaultVersionNo(version); // fixme: save or check version first
            meta.setPublicationStatus(documentDTO.getPublicationStatus());
            meta.setTarget(documentDTO.getTarget());
            meta.setDocumentType(documentDTO.getType());
            meta.setKeywords(documentDTO.getKeywords());

            // todo: check publication status and save here
            final AuditDTO publication = documentDTO.getPublished();
            meta.setPublisherId(publication.getId());
            meta.setPublicationStartDatetime(publication.getFormattedDate());

            final AuditDTO publicationEnd = documentDTO.getPublicationEnd();
            meta.setDepublisherId(publicationEnd.getId());
            meta.setPublicationEndDatetime(publicationEnd.getFormattedDate());

            final AuditDTO archivation = documentDTO.getArchived();
            meta.setArchiverId(archivation.getId());
            meta.setArchivedDatetime(archivation.getFormattedDate());

            final UserDomainObject currentUser = Imcms.getUser();

            // save creator to version too
            final AuditDTO creation = documentDTO.getCreated();
            final Integer creatorId = Optional.ofNullable(creation.getId()).orElseGet(currentUser::getId);
            meta.setCreatorId(creatorId);

            final Date currentDateTime = new Date();

            final Date createdDatetime = ((creation.getDate() == null) || (creation.getTime() == null))
                    ? currentDateTime : creation.getFormattedDate();

            meta.setCreatedDatetime(createdDatetime);

            final AuditDTO modification = documentDTO.getModified();
            final Integer modifierId = Optional.ofNullable(modification.getId()).orElseGet(currentUser::getId);
            meta.setModifierId(modifierId);

            final Date modifiedDatetime = ((modification.getDate() == null) || (modification.getTime() == null))
                    ? currentDateTime : modification.getFormattedDate();

            meta.setModifiedDatetime(modifiedDatetime);

            if (null == documentDTO.getId()) {
                meta.getProperties().put(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, documentDTO.getAlias());
            } else {
                List<Property> properties = propertyRepository.findByDocId(documentDTO.getId());
                if (properties.isEmpty()) {
                    meta.getProperties().put(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, documentDTO.getAlias());
                } else {
                    Map<String, String> propertiesMap = properties.stream().collect((Collectors.toMap(Property::getName, Property::getValue)));
                    meta.setProperties(propertiesMap);
                    if (meta.getProperties().containsKey(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)) {
                        meta.getProperties().replace(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, documentDTO.getAlias());
                    }
                }
            }


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

            meta.setRoleIdToPermission(documentDTO.getRoleIdToPermission());

            final Set<RestrictedPermissionJPA> restrictedPermissions = documentDTO.getRestrictedPermissions()
                    .stream()
                    .map(RestrictedPermissionJPA::new)
                    .collect(Collectors.toSet());

            meta.setRestrictedPermissions(restrictedPermissions);

            return meta;
        };
    }

    @Bean
    public TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> documentMapping(UserService userService) {
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

        return (meta, latestVersion, commonContents) -> {
            final DocumentDTO dto = new DocumentDTO();
            final Integer metaId = meta.getId();
            dto.setId(metaId);
            dto.setTarget(meta.getTarget());
            dto.setAlias(meta.getAlias());
            dto.setPublicationStatus(meta.getPublicationStatus());
            dto.setCommonContents(commonContents);
            dto.setPublished(auditDtoCreator.apply(meta::getPublisherId, meta::getPublicationStartDatetime));
            dto.setPublicationEnd(auditDtoCreator.apply(meta::getDepublisherId, meta::getPublicationEndDatetime));
            dto.setArchived(auditDtoCreator.apply(meta::getArchiverId, meta::getArchivedDatetime));
            dto.setCreated(auditDtoCreator.apply(meta::getCreatorId, meta::getCreatedDatetime));
            dto.setModified(auditDtoCreator.apply(meta::getModifierId, meta::getModifiedDatetime));
            dto.setDisabledLanguageShowMode(meta.getDisabledLanguageShowMode());
            dto.setCurrentVersion(AuditDTO.fromVersion(latestVersion));
            dto.setSearchDisabled(meta.isSearchDisabled());
            dto.setKeywords(meta.getKeywords());
            dto.setRoleIdToPermission(meta.getRoleIdToPermission());
            dto.setCategories(meta.getCategories());
            dto.setRestrictedPermissions(new HashSet<>(meta.getRestrictedPermissions()));
            dto.setProperties(meta.getProperties());
            dto.setType(meta.getDocumentType());
            dto.setLinkableByOtherUsers(meta.getLinkableByOtherUsers());
            dto.setLinkableForUnauthorizedUsers(meta.getLinkedForUnauthorizedUsers());

            return dto;
        };
    }

    @Bean
    @SneakyThrows
    public Function<File, ImageFileDTO> fileToImageFileDTO(@Value("${ImagePath}") Resource imagesPath) {
        final String imageRoot = imagesPath.getFile().getPath();

        return imageFile -> {
            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            final String fileName = imageFile.getName();

            imageFileDTO.setName(fileName);
            imageFileDTO.setFormat(Format.findFormat(FilenameUtils.getExtension(fileName)));

            final String relativePath = imageFile.getPath()
                    .replace(imageRoot, "")
                    .replace("\\", "/");

            imageFileDTO.setPath(relativePath);

            final Date lastModifiedDate = new Date(imageFile.lastModified());
            final String formattedDate = DateConstants.DATETIME_DOC_FORMAT.format(lastModifiedDate);

            imageFileDTO.setUploaded(formattedDate);

            imageFileDTO.setSize(getFileSize(imageFile));

            final Dimension imageDimension = ImcmsImageUtils.getImageDimension(imageFile);

            if (imageDimension != null) {
                imageFileDTO.setWidth(imageDimension.width);
                imageFileDTO.setHeight(imageDimension.height);
                imageFileDTO.setResolution(String.valueOf(imageDimension.width) + "x" + imageDimension.height);
            }

            return imageFileDTO;
        };
    }

    private String getFileSize(File file) {
        long fileSize = file.length();

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
    @SneakyThrows
    public BiFunction<File, Boolean, ImageFolderDTO> fileToImageFolderDTO(Function<File, ImageFileDTO> fileToImageFileDTO,
                                                                          @Value("${ImagePath}") Resource imagesPath) {
        final String imageRoot = imagesPath.getFile().getPath();

        return new BiFunction<File, Boolean, ImageFolderDTO>() {
            @Override
            public ImageFolderDTO apply(File folderFile, Boolean isRoot) {
                final ImageFolderDTO imageFolderDTO = new ImageFolderDTO();
                imageFolderDTO.setName(folderFile.getName());
                final String relativePath = folderFile.getPath().replace(imageRoot, "");
                imageFolderDTO.setPath(relativePath);
                final String generatedImagesPath = new File(imageRoot, ImcmsConstants.IMAGE_GENERATED_FOLDER).getPath();

                final ArrayList<ImageFolderDTO> subFolders = new ArrayList<>();
                final ArrayList<ImageFileDTO> folderFiles = new ArrayList<>();

                final File[] files = folderFile.listFiles();

                if (files == null) {
                    return imageFolderDTO;
                }

                for (File file : files) {
                    if (file.isDirectory() && !file.getPath().equals(generatedImagesPath)) {
                        subFolders.add(this.apply(file, false));

                    } else if (isRoot && Format.isImage(FilenameUtils.getExtension(file.getName()))) {
                        folderFiles.add(fileToImageFileDTO.apply(file));
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
                contents = null;
            }
            final String size = getFileSize(path.toFile());

            return new SourceFile(path.getFileName().toString(), physicalPath, path.toString(), fileType, contents, size);
        };
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
