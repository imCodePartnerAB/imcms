package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.domain.service.TypeSorterMenuService;
import com.imcode.imcms.sorted.TypeSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultTypeSortMenuService implements TypeSorterMenuService {

    private final MenuService menuService;

    @Autowired
    DefaultTypeSortMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public List<TypeSort> typesSortByNested(boolean nested) {

        final List<TypeSort> typeSorts;
        if (nested) {
            typeSorts = Arrays.asList(TypeSort.values());
        } else {
            typeSorts = Arrays.stream(TypeSort.values()).skip(1).collect(Collectors.toList());
        }
        return typeSorts;
    }
}
