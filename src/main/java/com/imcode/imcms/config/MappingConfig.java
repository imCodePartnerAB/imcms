package com.imcode.imcms.config;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.dto.ImageData.CropRegion;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.doc.Meta;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentStoredFields;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class MappingConfig {
    @Bean
    public Function<LoopEntryDTO, LoopEntry> loopEntryDtoToEntry() {
        return loopEntryDTO -> new LoopEntry(loopEntryDTO.getIndex(), loopEntryDTO.isEnabled());
    }

    @Bean
    public Function<LoopEntry, LoopEntryDTO> loopEntryToLoopEntryDTO() {
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
    public Function<Template, TemplateDTO> templateToTemplateDTO() {
        return template -> new TemplateDTO(template.getId(), template.getName(), template.isHidden());
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
            documentDTO.setTarget(null);
            documentDTO.setType(Meta.DocumentType.values()[documentFields.documentType()]);
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
    public BiFunction<LoopDTO, Version, Loop> loopDtoToLoop(Function<LoopEntryDTO, LoopEntry> loopEntryDtoToEntry) {
        return (loopDTO, version) -> {
            final List<LoopEntry> entries = Objects.requireNonNull(loopDTO)
                    .getEntries()
                    .stream()
                    .map(loopEntryDtoToEntry)
                    .collect(Collectors.toList());

            return new Loop(version, loopDTO.getIndex(), entries);
        };
    }

    @Bean
    public Function<MenuItem, MenuItemDTO> menuItemToDTO() {
        return new Function<MenuItem, MenuItemDTO>() {
            @Override
            public MenuItemDTO apply(MenuItem menuItem) {
                final MenuItemDTO menuItemDTO = new MenuItemDTO();
                menuItemDTO.setDocumentId(menuItem.getDocumentId());
                menuItemDTO.setChildren(menuItem.getChildren().stream()
                        .map(this)
                        .collect(Collectors.toList()));
                return menuItemDTO;
            }
        };
    }

    @Bean
    public Function<Menu, MenuDTO> menuToMenuDTO(Function<MenuItem, MenuItemDTO> menuItemToMenuItemDTO) {
        return menu -> {
            final MenuDTO menuDTO = new MenuDTO();
            menuDTO.setDocId(menu.getVersion().getDocId());
            menuDTO.setMenuId(menu.getNo());
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
    public Function<Loop, LoopDTO> loopToLoopDTO(Function<LoopEntry, LoopEntryDTO> loopEntryToDtoMapper) {
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
    public Function<Meta, DocumentDTO> documentMapping(VersionService versionService,
                                                       CommonContentService commonContentService) {
        return (meta) -> {
            final DocumentDTO dto = new DocumentDTO();
            dto.setId(meta.getId());
            dto.setTarget(meta.getTarget());
            dto.setAlias(meta.getProperties().get(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS));

            final Version latestVersion = versionService.getLatestVersion(meta.getId());
            final CommonContent commonContent = commonContentService
                    .getOrCreate(meta.getId(), latestVersion.getNo(), Imcms.getUser());
            dto.setTitle(commonContent.getHeadline());

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

}
