package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;

public class CreateCategoryCommand implements Serializable {
    private static final long serialVersionUID = 6363251556036885818L;
    
    private String createCategoryName;
    private int createCategoryType;
    
    
    public CreateCategoryCommand() {
    }
    
    
    public String getCreateCategoryName() {
        return createCategoryName;
    }
    
    public void setCreateCategoryName(String createCategoryName) {
        this.createCategoryName = createCategoryName;
    }
    
    public int getCreateCategoryType() {
        return createCategoryType;
    }
    
    public void setCreateCategoryType(int createCategoryType) {
        this.createCategoryType = createCategoryType;
    }
}
