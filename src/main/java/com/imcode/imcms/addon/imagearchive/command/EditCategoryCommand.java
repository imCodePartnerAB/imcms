package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;

public class EditCategoryCommand implements Serializable {
    private static final long serialVersionUID = -2594211214373510323L;
    
    private boolean showEditCategory;
    private int editCategoryId;
    private int editCategoryType;
    private String editCategoryName;
    
    
    public EditCategoryCommand() {
    }
    

    public int getEditCategoryId() {
        return editCategoryId;
    }

    public void setEditCategoryId(int editCategoryId) {
        this.editCategoryId = editCategoryId;
    }

    public int getEditCategoryType() {
        return editCategoryType;
    }

    public void setEditCategoryType(int editCategoryType) {
        this.editCategoryType = editCategoryType;
    }

    public String getEditCategoryName() {
        return editCategoryName;
    }

    public void setEditCategoryName(String editCategoryName) {
        this.editCategoryName = editCategoryName;
    }

    public boolean isShowEditCategory() {
        return showEditCategory;
    }

    public void setShowEditCategory(boolean showEditCategory) {
        this.showEditCategory = showEditCategory;
    }
}
