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
    private List<Integer> assignedCategoryIds;

    
    public SaveRoleCategoriesCommand() {
    }

    
    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        categoryIds = StringUtils.trimToNull(categoryIds);
        if (categoryIds != null) {
            String[] parts = categoryIds.split(",");
            assignedCategoryIds = new ArrayList<Integer>(parts.length);
            
            for (String part : parts) {
                try {
                    assignedCategoryIds.add(Integer.parseInt(part, 10));
                } catch (NumberFormatException ex) {
                }
            }
        }
        
        this.categoryIds = categoryIds;
    }

    public List<Integer> getAssignedCategoryIds() {
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
}
