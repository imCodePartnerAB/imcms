package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;

public class AddImageActionCommand implements Serializable {
    private static final long serialVersionUID = 1657813807721400555L;
    
    private String saveAction;
    private String addAction;
    private String useAction;
    private String imageCardAction;
    private String discontinueAction;
    
    
    public AddImageActionCommand() {
    }
    
    
    public boolean isSave() {
        return saveAction != null;
    }
    
    public boolean isAdd() {
        return addAction != null;
    }
    
    public boolean isUse() {
        return useAction != null;
    }
    
    public boolean isImageCard() {
        return imageCardAction != null;
    }
    
    public boolean isDiscontinue() {
        return discontinueAction != null;
    }
    
    public String getSaveAction() {
        return saveAction;
    }
    
    public void setSaveAction(String saveAction) {
        this.saveAction = saveAction;
    }
    
    public String getAddAction() {
        return addAction;
    }
    
    public void setAddAction(String addAction) {
        this.addAction = addAction;
    }
    
    public String getUseAction() {
        return useAction;
    }
    
    public void setUseAction(String useAction) {
        this.useAction = useAction;
    }
    
    public String getImageCardAction() {
        return imageCardAction;
    }
    
    public void setImageCardAction(String imageCardAction) {
        this.imageCardAction = imageCardAction;
    }
    
    public String getDiscontinueAction() {
        return discontinueAction;
    }
    
    public void setDiscontinueAction(String discontinueAction) {
        this.discontinueAction = discontinueAction;
    }
}
