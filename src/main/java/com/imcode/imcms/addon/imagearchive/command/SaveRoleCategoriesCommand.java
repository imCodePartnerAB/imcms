package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class SaveRoleCategoriesCommand implements Serializable {
    private static final long serialVersionUID = 8195144106210986876L;
    
    private boolean canUse;
    private boolean canChange;
    
    private String categoryIds;
    private List<CategoryRight> assignedCategoryIds;

    
    public SaveRoleCategoriesCommand() {
    }

    
    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        categoryIds = StringUtils.trimToNull(categoryIds);
        if (categoryIds != null) {
            String[] parts = categoryIds.split("-");
            assignedCategoryIds = new ArrayList<CategoryRight>(parts.length);
            
            for (String part : parts) {
                try {
                    // categoryId, canUse, canEdit
                    String[] catRightsPart = part.split(",");
                    if(catRightsPart.length == 3) {
                        CategoryRight categoryRight = new CategoryRight();
                        categoryRight.setCategoryId(Integer.parseInt(catRightsPart[0], 10));
                        categoryRight.setCanUse("1".equals(catRightsPart[1]));
                        categoryRight.setCanEditOrAdd("1".equals(catRightsPart[2]));
                        assignedCategoryIds.add(categoryRight);
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        
        this.categoryIds = categoryIds;
    }

    public List<CategoryRight> getAssignedCategoryIds() {
        return assignedCategoryIds;
    }
    
    
    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public class CategoryRight implements Serializable {

        public CategoryRight() {

        }

        public boolean isCanUse() {
            return canUse;
        }

        public void setCanUse(boolean canUse) {
            this.canUse = canUse;
        }

        public boolean isCanEditOrAdd() {
            return canEditOrAdd;
        }

        public void setCanEditOrAdd(boolean canEditOrAdd) {
            this.canEditOrAdd = canEditOrAdd;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        boolean canUse;
        boolean canEditOrAdd;
        Integer categoryId;
    }
}
