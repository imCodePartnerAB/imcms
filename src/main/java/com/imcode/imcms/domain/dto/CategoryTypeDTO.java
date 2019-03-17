package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CategoryTypeDTO extends CategoryType {

    private static final long serialVersionUID = -4636053716188761920L;

    private Integer id;

    private String name;

    private boolean multiSelect;

    private boolean inherited;

    public CategoryTypeDTO(CategoryType from) {
        super(from);
    }

    @Override
    public boolean isInherited() {
        return inherited;
    }

    @Override
    public void setInherited(boolean isInherited) {
        this.inherited = isInherited;
    }

    @Override
    public boolean isImageArchive() {
        return false;
    }

    @Override
    public void setImageArchive(boolean imageArchive) {
        // not implemented
    }
}
