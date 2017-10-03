package com.imcode.imcms.config;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry;
import com.imcode.imcms.persistence.entity.Category;
import com.imcode.imcms.persistence.entity.CategoryType;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MappingConfig {
    @Bean
    public Function<LoopEntryDTO, Entry> loopEntryDtoToEntry() {
        return loopEntryDTO -> new Entry(loopEntryDTO.getNo(), loopEntryDTO.isEnabled());
    }

    @Bean
    public Function<Entry, LoopEntryDTO> loopEntryToLoopEntryDTO() {
        return entry -> new LoopEntryDTO(entry.getNo(), entry.isEnabled());
    }

    @Bean
    public Function<Category, CategoryDTO> categoryToCategoryDTO() {
        return category -> new CategoryDTO(category.getId(), category.getName());
    }

    @Bean
    public BiFunction<LoopDTO, Version, Loop> loopDtoToLoop(Function<LoopEntryDTO, Entry> loopEntryDtoToEntry) {
        return (loopDTO, version) -> {
            final List<Entry> entries = Objects.requireNonNull(loopDTO)
                    .getEntries()
                    .stream()
                    .map(loopEntryDtoToEntry)
                    .collect(Collectors.toList());

            final int nextEntryNo = Math.max(1, entries.size());

            return new Loop(version, loopDTO.getLoopIndex(), nextEntryNo, entries);
        };
    }

    @Bean
    public Function<TreeMenuItemDomainObject, MenuElementDTO> treeMenuItemDomainObjectToMenuElementDTO() {
        return new Function<TreeMenuItemDomainObject, MenuElementDTO>() {
            @Override
            public MenuElementDTO apply(TreeMenuItemDomainObject treeMenuItemDomainObject) {
                final MenuItemDomainObject menuItem = treeMenuItemDomainObject.getMenuItem();

                final Integer id = menuItem.getId();
                final String title = menuItem.getDocument().getHeadline();
                final List<MenuElementDTO> children = treeMenuItemDomainObject
                        .getSubMenuItems()
                        .stream()
                        .map(this)
                        .collect(Collectors.toList());

                return new MenuElementDTO(id, title, children);
            }
        };
    }

    @Bean
    public Function<Loop, LoopDTO> loopToLoopDTO(Function<Entry, LoopEntryDTO> loopEntryToDtoMapper) {
        return loop -> {
            final List<LoopEntryDTO> loopEntryDTOs = Objects.requireNonNull(loop)
                    .getEntries()
                    .stream()
                    .map(loopEntryToDtoMapper)
                    .collect(Collectors.toList());

            return new LoopDTO(loop.getVersion().getDocId(), loop.getNo(), loopEntryDTOs);
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
}
