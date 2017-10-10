package com.imcode.imcms.util.mapping;

import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.MenuItem;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Only for static methods invocation.
 */
public final class MappingUtils {
    private MappingUtils() {
    }

    public static List<MenuItem> mapMenuItemDtoListToMenuItem(List<MenuItemDTO> menuItems, Function<MenuItemDTO, MenuItem> menuItemDtoToMenuItem) {
        return IntStream.range(0, menuItems.size())
                .mapToObj(i -> {
                    final MenuItem menuItem = menuItemDtoToMenuItem.apply(menuItems.get(i));
                    menuItem.setSortOrder(i + 1);
                    return menuItem;
                })
                .collect(Collectors.toList());
    }

}
