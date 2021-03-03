package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.TypeSorterMenuService;
import com.imcode.imcms.enums.TypeSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/types-sort")
public class TypeSortController {

    private final TypeSorterMenuService typeSorterMenuService;

    @Autowired
    public TypeSortController(TypeSorterMenuService typeSorterMenuService) {
        this.typeSorterMenuService = typeSorterMenuService;
    }


    @GetMapping
    public List<TypeSort> getSortTypes(@RequestParam boolean nested) {
        return typeSorterMenuService.typesSortByNested(nested);
    }
}
