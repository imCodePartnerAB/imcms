package com.imcode.imcms.config;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.dto.ImageData.ImageCropRegionDTO;
import com.imcode.imcms.domain.service.api.CategoryService;
import com.imcode.imcms.domain.service.api.DocumentService;
import com.imcode.imcms.domain.service.api.RoleService;
import com.imcode.imcms.domain.service.api.UserService;
import com.imcode.imcms.domain.service.core.TextDocumentTemplateService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.document.index.DocumentStoredFields;
import imcode.util.DateConstants;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static imcode.server.document.DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS;

/**
 * Configuration class for mapping DTO -> JPA and vice versa, but not only.
 */
@Configuration
class MappingConfig {

    @Bean
    public Function<User, UserDTO> userToUserDTO() {
        return user -> new UserDTO(user.getId(), user.getLogin());
    }

    @Bean
    public Function<DocumentStoredFields, DocumentDTO> documentStoredFieldToDocumentDto() {
        return documentFields -> {
            final DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setId(documentFields.id());
            documentDTO.setAlias(documentFields.alias());
            documentDTO.setTitle(documentFields.headline());
            documentDTO.setType(DocumentType.values()[documentFields.documentType()]);
            return documentDTO;
        };
    }

    @Bean
    public Function<MenuItem, MenuItemDTO> menuItemToDTO(DocumentService documentService) {
        return new Function<MenuItem, MenuItemDTO>() {
            @Override
            public MenuItemDTO apply(MenuItem menuItem) {
                final Integer documentId = menuItem.getDocumentId();

                final MenuItemDTO menuItemDTO = new MenuItemDTO();
                menuItemDTO.setDocumentId(documentId);
                menuItemDTO.setTitle(documentService.getDocumentTitle(documentId));
                menuItemDTO.setLink(documentService.getDocumentLink(documentId));
                menuItemDTO.setTarget(documentService.getDocumentTarget(documentId));

                final List<MenuItemDTO> children = menuItem.getChildren()
                        .stream()
                        .map(this)
                        .collect(Collectors.toList());

                menuItemDTO.setChildren(children);
                return menuItemDTO;
            }
        };
    }

    @Bean
    public Function<Menu, MenuDTO> menuToMenuDTO(Function<MenuItem, MenuItemDTO> menuItemToMenuItemDTO) {
        return menu -> {
            final MenuDTO menuDTO = new MenuDTO();
            menuDTO.setDocId(menu.getVersion().getDocId());
            menuDTO.setMenuIndex(menu.getNo());
            menuDTO.setMenuItems(menu.getMenuItems().stream().map(menuItemToMenuItemDTO).collect(Collectors.toList()));

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
                final List<MenuItem> children = menuItemDtoListToMenuItemList(this).apply(menuItemDTO.getChildren());
                menuItem.setChildren(children);
                return menuItem;
            }
        };
    }

    @Bean
    public Function<List<MenuItemDTO>, List<MenuItem>> menuItemDtoListToMenuItemList(Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem) {
        return menuItemDtoList -> IntStream.range(0, menuItemDtoList.size())
                .mapToObj(i -> {
                    final MenuItem menuItem = menuItemDtoToMenuItem.apply(menuItemDtoList.get(i));
                    menuItem.setSortOrder(i + 1);
                    return menuItem;
                })
                .collect(Collectors.toList());
    }

    @Bean
    public Function<Image, ImageDTO> imageToImageDTO(@Value("${ImageUrl}") String imagesPath) {
        return image -> {
            final ImageDTO dto = new ImageDTO();

            dto.setIndex(image.getIndex());
            dto.setName(image.getName());
            dto.setDocId(image.getVersion().getDocId());
            dto.setLangCode(image.getLanguage().getCode());
            dto.setPath(image.getUrl());

            final String generatedFilePath = (image.getGeneratedFilename() == null)
                    ? "" : imagesPath + "generated/" + image.getGeneratedFilename();

            dto.setGeneratedFilePath(generatedFilePath);
            dto.setGeneratedFilename(image.getGeneratedFilename());
            dto.setFormat(image.getFormat());
            dto.setHeight(image.getHeight());
            dto.setWidth(image.getWidth());
            Optional.ofNullable(image.getLoopEntryRef()).map(LoopEntryRefDTO::new).ifPresent(dto::setLoopEntryRef);
            dto.setCropRegion(new ImageCropRegionDTO(image.getCropRegion()));

            return dto;
        };
    }

    @Bean
    public TernaryFunction<ImageDTO, Version, LanguageJPA, Image> imageDtoToImage() {

        return (imageDTO, version, language) -> {
            final Image image = new Image();
            image.setIndex(imageDTO.getIndex());
            image.setVersion(version);
            image.setLanguage(language);
            image.setHeight(imageDTO.getHeight());
            image.setWidth(imageDTO.getWidth());
            image.setUrl(imageDTO.getPath());
            image.setName(imageDTO.getName());
            image.setGeneratedFilename(imageDTO.getGeneratedFilename());
            Optional.ofNullable(imageDTO.getLoopEntryRef()).map(LoopEntryRefJPA::new).ifPresent(image::setLoopEntryRef);
            image.setFormat(imageDTO.getFormat());
            image.setCropRegion(new ImageCropRegionJPA(imageDTO.getCropRegion()));

            return image;
        };
    }

    @Bean
    public Function<RestrictedPermissionJPA, PermissionDTO> restrictedPermissionToPermissionDTO() {
        return restrictedPermission -> PermissionDTO.fromPermission(restrictedPermission.getPermission());
    }

    @Bean
    public Function<RestrictedPermissionJPA, RestrictedPermissionDTO> restrictedPermissionToDto() {
        return restrictedPermission -> {
            final RestrictedPermissionDTO permissionDTO = new RestrictedPermissionDTO();

            permissionDTO.setEditDocumentInfo(restrictedPermission.isEditDocInfo());
            permissionDTO.setEditImage(restrictedPermission.isEditImage());
            permissionDTO.setEditLoop(restrictedPermission.isEditLoop());
            permissionDTO.setEditMenu(restrictedPermission.isEditMenu());
            permissionDTO.setEditText(restrictedPermission.isEditText());

            return permissionDTO;
        };
    }

    @Bean
    public Function<Set<RestrictedPermissionJPA>, Map<PermissionDTO, RestrictedPermissionDTO>> restrictedPermissionsToDTO(
            Function<RestrictedPermissionJPA, PermissionDTO> restrictedPermissionToPermissionDTO,
            Function<RestrictedPermissionJPA, RestrictedPermissionDTO> restrictedPermissionToRestrictedPermissionDTO
    ) {
        return restrictedPermissions -> restrictedPermissions.stream().collect(
                Collectors.toMap(restrictedPermissionToPermissionDTO, restrictedPermissionToRestrictedPermissionDTO)
        );
    }

    @Bean
    public Function<Map<PermissionDTO, RestrictedPermissionDTO>, Set<RestrictedPermissionJPA>>
    restrictedPermissionsDtoToRestrictedPermissions() {
        return restrictedPermissions -> restrictedPermissions.entrySet()
                .stream()
                .map(permissionDtoToRestrictedDto -> {
                    final PermissionDTO permissionDTO = permissionDtoToRestrictedDto.getKey();
                    final RestrictedPermissionDTO restrictedPermissionDTO = permissionDtoToRestrictedDto.getValue();

                    final RestrictedPermissionJPA restrictedPermission = new RestrictedPermissionJPA();

                    restrictedPermission.setPermission(permissionDTO.getPermission());
                    restrictedPermission.setEditDocInfo(restrictedPermissionDTO.isEditDocumentInfo());
                    restrictedPermission.setEditImage(restrictedPermissionDTO.isEditImage());
                    restrictedPermission.setEditLoop(restrictedPermissionDTO.isEditLoop());
                    restrictedPermission.setEditMenu(restrictedPermissionDTO.isEditMenu());
                    restrictedPermission.setEditText(restrictedPermissionDTO.isEditText());

                    return restrictedPermission;
                })
                .collect(Collectors.toSet());
    }

    @Bean
    public Function<Map<Integer, Meta.Permission>, Set<RoleDTO>> roleIdByPermissionToRoleDTOs(RoleService roleService) {
        return integerPermissionMap -> integerPermissionMap.entrySet()
                .stream()
                .map(roleIdToPermission -> {
                    final Integer roleId = roleIdToPermission.getKey();
                    final Meta.Permission rolePermission = roleIdToPermission.getValue();
                    final RoleDTO role = roleService.getById(roleId);
                    role.setPermission(PermissionDTO.fromPermission(rolePermission));
                    return role;
                })
                .collect(Collectors.toSet());
    }

    @Bean
    public Function<Set<RoleDTO>, Map<Integer, Meta.Permission>> rolesDtoToRoleIdByPermission() {
        return roleDTOS -> roleDTOS.stream().collect(Collectors.toMap(RoleDTO::getId,
                roleDTO -> roleDTO.getPermission().getPermission()
        ));
    }

    @Bean
    public Function<DocumentDTO, Meta> documentDtoToMeta(
            Function<Set<RoleDTO>, Map<Integer, Meta.Permission>> rolesDtoToRoleIdByPermission,
            Function<Map<PermissionDTO, RestrictedPermissionDTO>, Set<RestrictedPermissionJPA>>
                    restrictedPermissionsDtoToRestrictedPermissions
    ) {
        return documentDTO -> {
            final Meta meta = new Meta();
            final int docId = documentDTO.getId();
            final int version = documentDTO.getCurrentVersion().getId();

            meta.setId(docId);
            meta.setDefaultVersionNo(version); // fixme: save or check version first
            meta.setPublicationStatus(documentDTO.getPublicationStatus());
            meta.setTarget(documentDTO.getTarget());
            meta.setDocumentType(documentDTO.getType());
            meta.setKeywords(documentDTO.getKeywords());

            final AuditDTO publication = documentDTO.getPublished();
            meta.setPublisherId(publication.getId());
            meta.setPublicationStartDatetime(publication.getFormattedDate());

            final AuditDTO publicationEnd = documentDTO.getPublicationEnd();
            meta.setDepublisherId(publicationEnd.getId());
            meta.setPublicationEndDatetime(publicationEnd.getFormattedDate());

            final AuditDTO archivation = documentDTO.getArchived();
            meta.setArchiverId(archivation.getId());
            meta.setArchivedDatetime(archivation.getFormattedDate());

            // save creator to version too
            final AuditDTO creation = documentDTO.getCreated();
            meta.setCreatorId(creation.getId());
            meta.setCreatedDatetime(creation.getFormattedDate());

            final AuditDTO modification = documentDTO.getModified();
            meta.setModifierId(modification.getId());
            meta.setModifiedDatetime(modification.getFormattedDate());

            meta.getProperties().put(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, documentDTO.getAlias());

            meta.setDisabledLanguageShowMode(documentDTO.getDisabledLanguageShowMode());
            meta.setSearchDisabled(documentDTO.isSearchDisabled());

            final Set<Integer> categoryIds = documentDTO.getCategories()
                    .stream()
                    .map(CategoryDTO::getId)
                    .collect(Collectors.toSet());

            meta.setCategoryIds(categoryIds);

            meta.setLinkableByOtherUsers(true);                         // fixme: not sure what to do with this
            meta.setLinkedForUnauthorizedUsers(true);                   // fixme: not sure what to do with this

            meta.setRoleIdToPermission(rolesDtoToRoleIdByPermission.apply(documentDTO.getRoles()));

            meta.setRestrictedPermissions(restrictedPermissionsDtoToRestrictedPermissions.apply(
                    documentDTO.getRestrictedPermissions()
            ));

            return meta;
        };
    }

    @Bean
    public TernaryFunction<Meta, Version, List<CommonContentDTO>, DocumentDTO> documentMapping(
            Function<Set<RestrictedPermissionJPA>, Map<PermissionDTO, RestrictedPermissionDTO>> restrictedPermissionsToDTO,
            Function<Map<Integer, Meta.Permission>, Set<RoleDTO>> roleIdByPermissionToRoleDTOs,
            CategoryService categoryService,
            UserService userService,
            TextDocumentTemplateService textDocumentTemplateService
    ) {
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
            dto.setAlias(meta.getProperties().get(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS));
            dto.setPublicationStatus(meta.getPublicationStatus());
            dto.setType(meta.getDocumentType());
            dto.setCommonContents(commonContents);
            dto.setPublished(auditDtoCreator.apply(meta::getPublisherId, meta::getPublicationStartDatetime));
            dto.setPublicationEnd(auditDtoCreator.apply(meta::getDepublisherId, meta::getPublicationEndDatetime));
            dto.setArchived(auditDtoCreator.apply(meta::getArchiverId, meta::getArchivedDatetime));
            dto.setCreated(auditDtoCreator.apply(meta::getCreatorId, meta::getCreatedDatetime));
            dto.setModified(auditDtoCreator.apply(meta::getModifierId, meta::getModifiedDatetime));
            dto.setDisabledLanguageShowMode(meta.getDisabledLanguageShowMode());

            final AuditDTO versionAudit = new AuditDTO();
            versionAudit.setDateTime(latestVersion.getCreatedDt());
            versionAudit.setId(latestVersion.getNo());
            versionAudit.setBy(latestVersion.getModifiedBy().getLogin());
            dto.setCurrentVersion(versionAudit);
            dto.setSearchDisabled(meta.isSearchDisabled());

            dto.setKeywords(meta.getKeywords());

            final Set<RoleDTO> rolesDTO = roleIdByPermissionToRoleDTOs.apply(meta.getRoleIdToPermission());
            dto.setRoles(rolesDTO);

            final Set<CategoryDTO> categories = meta.getCategoryIds()
                    .stream()
                    .map(categoryService::getById)
                    .collect(Collectors.toSet());

            dto.setCategories(categories);
            dto.setRestrictedPermissions(restrictedPermissionsToDTO.apply(meta.getRestrictedPermissions()));

            textDocumentTemplateService.get(metaId).ifPresent(dto::setTemplate);

            return dto;
        };
    }

    @Bean
    public Function<File, ImageFileDTO> fileToImageFileDTO(@Value("${ImagePath}") File imagesPath) {
        return imageFile -> {
            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            final String fileName = imageFile.getName();

            imageFileDTO.setName(fileName);
            imageFileDTO.setFormat(Format.findFormat(FilenameUtils.getExtension(fileName)));

            final String relativePath = imageFile.getPath()
                    .replace(imagesPath.getPath(), "")
                    .replace("\\", "/");

            imageFileDTO.setPath(relativePath);

            final Date lastModifiedDate = new Date(imageFile.lastModified());
            final String formattedDate = DateConstants.DATETIME_DOC_FORMAT.format(lastModifiedDate);

            imageFileDTO.setUploaded(formattedDate);

            long fileSize = imageFile.length();
            String suffix;

            if (fileSize >= (1024L * 1024L)) {
                suffix = "MB";
                fileSize /= 1024L * 1024L;

            } else if (fileSize >= 1024L) {
                suffix = "kB";
                fileSize /= 1024L;

            } else {
                suffix = "B";
            }

            imageFileDTO.setSize(String.valueOf(fileSize) + suffix);

            final java.awt.Dimension imageDimension = ImcmsImageUtils.getImageDimension(imageFile);

            if (imageDimension != null) {
                imageFileDTO.setWidth(imageDimension.width);
                imageFileDTO.setHeight(imageDimension.height);
                imageFileDTO.setResolution(String.valueOf(imageDimension.width) + "x" + imageDimension.height);
            }

            return imageFileDTO;
        };
    }

    @Bean
    public Function<File, ImageFolderDTO> fileToImageFolderDTO(Function<File, ImageFileDTO> fileToImageFileDTO,
                                                               @Value("${ImagePath}") File imagesPath) {
        return new Function<File, ImageFolderDTO>() {
            @Override
            public ImageFolderDTO apply(File folderFile) {
                final ImageFolderDTO imageFolderDTO = new ImageFolderDTO();
                imageFolderDTO.setName(folderFile.getName());
                final String relativePath = folderFile.getPath().replace(imagesPath.getPath(), "");
                imageFolderDTO.setPath(relativePath);

                final ArrayList<ImageFolderDTO> subFolders = new ArrayList<>();
                final ArrayList<ImageFileDTO> folderFiles = new ArrayList<>();

                final File[] files = folderFile.listFiles();

                if (files == null) {
                    return imageFolderDTO;
                }

                for (File file : files) {
                    if ((file.isDirectory())) {
                        subFolders.add(this.apply(file));

                    } else if (Format.isImage(FilenameUtils.getExtension(file.getName()))) {
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
    public Function<Text, TextDTO> textToTextDTO() {
        return text -> {
            final TextDTO textDTO = new TextDTO();

            textDTO.setDocId(text.getVersion().getDocId());
            textDTO.setIndex(text.getIndex());
            Optional.ofNullable(text.getLoopEntryRef()).map(LoopEntryRefDTO::new).ifPresent(textDTO::setLoopEntryRef);
            textDTO.setLangCode(text.getLanguage().getCode());
            textDTO.setText(text.getText());
            textDTO.setType(text.getType());

            return textDTO;
        };
    }

    @Bean
    public TernaryFunction<TextDTO, Version, LanguageJPA, Text> textDtoToText() {
        return (textDTO, version, language) -> {
            final Text text = new Text();
            text.setIndex(textDTO.getIndex());
            text.setVersion(version);
            text.setLanguage(language);
            Optional.ofNullable(textDTO.getLoopEntryRef()).map(LoopEntryRefJPA::new).ifPresent(text::setLoopEntryRef);
            text.setType(textDTO.getType());
            text.setText(textDTO.getText());

            return text;
        };
    }

}
