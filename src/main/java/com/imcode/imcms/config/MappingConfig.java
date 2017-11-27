package com.imcode.imcms.config;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.dto.ImageData.CropRegion;
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
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
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
    public Function<LoopEntryDTO, LoopEntryJPA> loopEntryDtoToEntry() {
        return loopEntryDTO -> new LoopEntryJPA(loopEntryDTO.getIndex(), loopEntryDTO.isEnabled());
    }

    @Bean
    public Function<LoopEntryJPA, LoopEntryDTO> loopEntryToLoopEntryDTO() {
        return entry -> new LoopEntryDTO(entry.getIndex(), entry.isEnabled());
    }

    @Bean
    public Function<Category, CategoryDTO> categoryToCategoryDTO() {
        return category -> new CategoryDTO(category.getId(), category.getName());
    }

    @Bean
    public Function<Role, RoleDTO> roleToRoleDTO() {
        return role -> new RoleDTO(role.getId(), role.getName());
    }

    @Bean
    public Function<RoleDTO, Role> roleDtoToRole() {
        return roleDTO -> {
            final Role role = new Role();
            role.setId(roleDTO.getId());
            role.setName(roleDTO.getName());
            return role;
        };
    }

    @Bean
    public Function<TemplateJPA, TemplateDTO> templateToTemplateDTO() {
        return TemplateDTO::new;
    }

    @Bean
    public Function<TemplateDTO, TemplateJPA> templateDtoToTemplate() {
        return TemplateJPA::new;
    }

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
    public Function<Language, LanguageDTO> languageToLanguageDTO() {
        return language -> {
            final LanguageDTO languageDTO = new LanguageDTO();
            BeanUtils.copyProperties(language, languageDTO);
            return languageDTO;
        };
    }

    @Bean
    public Function<LanguageDTO, Language> languageDtoToLanguage() {
        return languageDTO -> {
            final Language language = new Language();
            BeanUtils.copyProperties(languageDTO, language);
            return language;
        };
    }

    @Bean
    public BiFunction<LoopDTO, Version, Loop> loopDtoToLoop(Function<LoopEntryDTO, LoopEntryJPA> loopEntryDtoToEntry) {
        return (loopDTO, version) -> {
            final List<LoopEntryJPA> entries = Objects.requireNonNull(loopDTO)
                    .getEntries()
                    .stream()
                    .map(loopEntryDtoToEntry)
                    .collect(Collectors.toList());

            return new Loop(version, loopDTO.getIndex(), entries);
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
    public Function<Loop, LoopDTO> loopToLoopDTO(Function<LoopEntryJPA, LoopEntryDTO> loopEntryToDtoMapper) {
        return loop -> {
            final List<LoopEntryDTO> loopEntryDTOs = Objects.requireNonNull(loop)
                    .getEntries()
                    .stream()
                    .map(loopEntryToDtoMapper)
                    .collect(Collectors.toList());

            return new LoopDTO(loop.getVersion().getDocId(), loop.getIndex(), loopEntryDTOs);
        };
    }

    @Bean
    public Function<CategoryType, CategoryTypeDTO> categoryTypeToCategoryTypeDTO(Function<Category, CategoryDTO> categoryMapper) {
        return categoryType -> new CategoryTypeDTO(
                categoryType.getId(),
                categoryType.getName(),
                categoryType.isMultiSelect(),
                categoryType.getCategories()
                        .stream()
                        .map(categoryMapper)
                        .collect(Collectors.toList())
        );
    }

    @Bean
    public Function<LoopEntryRef, LoopEntryRefDTO> loopEntryRefToDTO() {
        return loopEntryRef -> (loopEntryRef == null) ? null
                : new LoopEntryRefDTO(loopEntryRef.getLoopIndex(), loopEntryRef.getLoopEntryIndex());
    }

    @Bean
    public Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef() {
        return loopEntryRefDTO -> (loopEntryRefDTO == null) ? null
                : new LoopEntryRef(loopEntryRefDTO.getLoopIndex(), loopEntryRefDTO.getLoopEntryIndex());
    }

    @Bean
    public Function<ImageCropRegion, CropRegion> imageCropRegionToCropRegionDTO() {
        return CropRegion::of;
    }

    @Bean
    public Function<CropRegion, ImageCropRegion> cropRegionDtoToImageCropRegion() {
        return ImageCropRegion::of;
    }

    @Bean
    public Function<Image, ImageDTO> imageToImageDTO(@Value("${ImageUrl}") String imagesPath,
                                                     Function<LoopEntryRef, LoopEntryRefDTO> loopEntryRefToDTO,
                                                     Function<ImageCropRegion, CropRegion> imageCropRegionToCropRegionDTO) {
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
            dto.setLoopEntryRef(loopEntryRefToDTO.apply(image.getLoopEntryRef()));
            dto.setCropRegion(imageCropRegionToCropRegionDTO.apply(image.getCropRegion()));

            return dto;
        };
    }

    @Bean
    public TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage(
            Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef,
            Function<CropRegion, ImageCropRegion> cropRegionDtoToImageCropRegion
    ) {

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
            image.setLoopEntryRef(loopEntryRefDtoToLoopEntryRef.apply(imageDTO.getLoopEntryRef()));
            image.setFormat(imageDTO.getFormat());
            image.setCropRegion(cropRegionDtoToImageCropRegion.apply(imageDTO.getCropRegion()));

            return image;
        };
    }

    @Bean
    public Function<CommonContentDTO, CommonContentJPA> dtoToCommonContent(Function<LanguageDTO, Language> dtoToLanguage) {
        return commonContentDTO -> new CommonContentJPA(commonContentDTO, dtoToLanguage.apply(commonContentDTO.getLanguage()));
    }

    @Bean
    public Function<CommonContentJPA, CommonContentDTO> commonContentToDTO(Function<Language, LanguageDTO> languageToDTO) {
        return commonContent -> new CommonContentDTO(commonContent, languageToDTO.apply(commonContent.getLanguage()));
    }

    @Bean
    public Function<RestrictedPermission, PermissionDTO> restrictedPermissionToPermissionDTO() {
        return restrictedPermission -> PermissionDTO.fromPermission(restrictedPermission.getPermission());
    }

    @Bean
    public Function<RestrictedPermission, RestrictedPermissionDTO> restrictedPermissionToDto() {
        return restrictedPermission -> {
            final RestrictedPermissionDTO permissionDTO = new RestrictedPermissionDTO();

            permissionDTO.setEditDocumentInfo(restrictedPermission.getEditDocInfo());
            permissionDTO.setEditImage(restrictedPermission.getEditImage());
            permissionDTO.setEditLoop(restrictedPermission.getEditLoop());
            permissionDTO.setEditMenu(restrictedPermission.getEditMenu());
            permissionDTO.setEditText(restrictedPermission.getEditText());

            return permissionDTO;
        };
    }

    @Bean
    public Function<Set<RestrictedPermission>, Map<PermissionDTO, RestrictedPermissionDTO>> restrictedPermissionsToDTO(
            Function<RestrictedPermission, PermissionDTO> restrictedPermissionToPermissionDTO,
            Function<RestrictedPermission, RestrictedPermissionDTO> restrictedPermissionToRestrictedPermissionDTO
    ) {
        return restrictedPermissions -> restrictedPermissions.stream().collect(
                Collectors.toMap(restrictedPermissionToPermissionDTO, restrictedPermissionToRestrictedPermissionDTO)
        );
    }

    @Bean
    public Function<Map<PermissionDTO, RestrictedPermissionDTO>, Set<RestrictedPermission>>
    restrictedPermissionsDtoToRestrictedPermissions() {
        return restrictedPermissions -> restrictedPermissions.entrySet()
                .stream()
                .map(permissionDtoToRestrictedDto -> {
                    final PermissionDTO permissionDTO = permissionDtoToRestrictedDto.getKey();
                    final RestrictedPermissionDTO restrictedPermissionDTO = permissionDtoToRestrictedDto.getValue();

                    final RestrictedPermission restrictedPermission = new RestrictedPermission();

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
            Function<Map<PermissionDTO, RestrictedPermissionDTO>, Set<RestrictedPermission>>
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
            Function<Set<RestrictedPermission>, Map<PermissionDTO, RestrictedPermissionDTO>> restrictedPermissionsToDTO,
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
    public Function<File, ImageFileDTO> fileToImageFileDTO() {
        return ImcmsImageUtils::fileToImageFileDTO;
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
    public Function<Text, TextDTO> textToTextDTO(Function<LoopEntryRef, LoopEntryRefDTO> loopEntryRefToDTO) {
        return text -> {
            final TextDTO textDTO = new TextDTO();

            textDTO.setDocId(text.getVersion().getDocId());
            textDTO.setIndex(text.getIndex());
            textDTO.setLoopEntryRef(loopEntryRefToDTO.apply(text.getLoopEntryRef()));
            textDTO.setLangCode(text.getLanguage().getCode());
            textDTO.setText(text.getText());
            textDTO.setType(text.getType());

            return textDTO;
        };
    }

    @Bean
    public TernaryFunction<TextDTO, Version, Language, Text> textDtoToText(
            Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef
    ) {
        return (textDTO, version, language) -> {
            final Text text = new Text();
            text.setIndex(textDTO.getIndex());
            text.setVersion(version);
            text.setLanguage(language);
            text.setLoopEntryRef(loopEntryRefDtoToLoopEntryRef.apply(textDTO.getLoopEntryRef()));
            text.setType(textDTO.getType());
            text.setText(textDTO.getText());

            return text;
        };
    }

}
