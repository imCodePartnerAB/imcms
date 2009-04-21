package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;

public class ImageCardChangeActionCommand implements Serializable {
    private static final long serialVersionUID = -9116196770276939398L;
    
    private String uploadAction;
    private String saveAction;
    private String useAction;
    private String imageCardAction;
    private String cancelAction;
    
    
    public ImageCardChangeActionCommand() {
    }
    
    public boolean isSet() {
        return isUpload() || isSave() || isUse() || isImageCard() || isCancel();
    }
    
    public boolean isUpload() {
        return uploadAction != null;
    }
    
    public boolean isSave() {
        return saveAction != null;
    }
    
    public boolean isUse() {
        return useAction != null;
    }
    
    public boolean isImageCard() {
        return imageCardAction != null;
    }
    
    public boolean isCancel() {
        return cancelAction != null;
    }
    
    public String getUploadAction() {
        return uploadAction;
    }

    public void setUploadAction(String uploadAction) {
        this.uploadAction = uploadAction;
    }

    public String getSaveAction() {
        return saveAction;
    }
    
    public void setSaveAction(String saveAction) {
        this.saveAction = saveAction;
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
    
    public String getCancelAction() {
        return cancelAction;
    }
    
    public void setCancelAction(String cancelAction) {
        this.cancelAction = cancelAction;
    }
}
