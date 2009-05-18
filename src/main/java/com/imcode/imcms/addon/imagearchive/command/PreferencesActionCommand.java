package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;

public class PreferencesActionCommand implements Serializable {
    private static final long serialVersionUID = -4311288116403084446L;
    
    private String saveRoleCategoriesAction;
    private String saveLibraryRolesAction;
    private String createCategoryAction;
    
    private String editCategoryAction;
    private String saveCategoryAction;
    private String removeCategoryAction;
    
    
    public PreferencesActionCommand() {
    }

    
    public boolean isSaveRoleCategories() {
        return saveRoleCategoriesAction != null;
    }
    
    public boolean isSaveLibraryRoles() {
        return saveLibraryRolesAction != null;
    }
    
    public boolean isCreateCategory() {
        return createCategoryAction != null;
    }
    
    public boolean isEditCategory() {
        return editCategoryAction != null;
    }
    
    public boolean isSaveCategory() {
        return saveCategoryAction != null;
    }
    
    public boolean isRemoveCategory() {
        return removeCategoryAction != null;
    }
    
    public String getSaveRoleCategoriesAction() {
        return saveRoleCategoriesAction;
    }

    public void setSaveRoleCategoriesAction(String saveRoleCategoriesAction) {
        this.saveRoleCategoriesAction = saveRoleCategoriesAction;
    }

    public String getSaveLibraryRolesAction() {
        return saveLibraryRolesAction;
    }

    public void setSaveLibraryRolesAction(String saveLibraryRolesAction) {
        this.saveLibraryRolesAction = saveLibraryRolesAction;
    }

    public String getCreateCategoryAction() {
        return createCategoryAction;
    }

    public void setCreateCategoryAction(String createCategoryAction) {
        this.createCategoryAction = createCategoryAction;
    }

    public String getSaveCategoryAction() {
        return saveCategoryAction;
    }
    
    public void setSaveCategoryAction(String saveCategoryAction) {
        this.saveCategoryAction = saveCategoryAction;
    }

    public String getRemoveCategoryAction() {
        return removeCategoryAction;
    }

    public void setRemoveCategoryAction(String removeCategoryAction) {
        this.removeCategoryAction = removeCategoryAction;
    }

    public String getEditCategoryAction() {
        return editCategoryAction;
    }

    public void setEditCategoryAction(String editCategoryAction) {
        this.editCategoryAction = editCategoryAction;
    }
}
