package com.imcode.imcms.config;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.util.function.TernaryFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                menuItemDTO.setId(menuItem.getId());
                menuItemDTO.setDocumentId(menuItem.getDocumentId());
                menuItemDTO.setChildren(menuItem.getChildren().stream()
                        .map(this)
                        .collect(Collectors.toList()));
                return menuItemDTO;
            }
        };
    }

    @Bean
    public Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem() {
        return new Function<MenuItemDTO, MenuItem>() {
            @Override
            public MenuItem apply(MenuItemDTO menuItemDTO) {
                final MenuItem menuItem = new MenuItem();
                menuItem.setId(menuItemDTO.getId());
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
    public Function<Image, ImageDTO> imageToImageDTO(@Value("${ImageUrl}") String imagesPath,
                                                     Function<LoopEntryRef, LoopEntryRefDTO> loopEntryRefToDTO) {
        return image -> {
            final ImageDTO dto = new ImageDTO();
            dto.setIndex(image.getIndex());

            final String name = image.getName();
            dto.setName(name);

            final String generatedFilePath = (image.getGeneratedFilename() == null)
                    ? "" : imagesPath + "generated/" + image.getGeneratedFilename();

            dto.setDocId(image.getVersion().getDocId());
            dto.setLangCode(image.getLanguage().getCode());
            dto.setPath(image.getUrl());
            dto.setGeneratedFilePath(generatedFilePath);
            dto.setGeneratedFilename(image.getGeneratedFilename());
            dto.setFormat(image.getFormat());
            dto.setHeight(image.getHeight());
            dto.setWidth(image.getWidth());
            dto.setLoopEntryRef(loopEntryRefToDTO.apply(image.getLoopEntryRef()));

            return dto;
        };
    }

    @Bean
    public TernaryFunction<ImageDTO, Version, Language, Image> imageDtoToImage(Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef) {
        return (imageDTO, version, language) -> {
            final Image image = new Image();
            image.setIndex(imageDTO.getIndex());
            image.setVersion(version);
            image.setLanguage(language);
            image.setHeight(imageDTO.getHeight());
            image.setWidth(imageDTO.getWidth());
            image.setUrl(imageDTO.getPath());
            image.setGeneratedFilename(imageDTO.getGeneratedFilename());
            image.setLoopEntryRef(loopEntryRefDtoToLoopEntryRef.apply(imageDTO.getLoopEntryRef()));
            image.setFormat(imageDTO.getFormat());

            return image;
        };
    }
}
