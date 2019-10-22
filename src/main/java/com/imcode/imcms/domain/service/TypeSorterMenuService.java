package com.imcode.imcms.domain.service;

import com.imcode.imcms.sorted.TypeSort;

import java.util.List;

public interface TypeSorterMenuService {

    List<TypeSort> typesSortByNested(boolean nested);
}
