package com.imcode.imcms.addon.imagearchive.command;

import java.io.Serializable;

public class ExternalFilesSaveImageCommand implements Serializable {
    private static final long serialVersionUID = 8285187972176419902L;
    
    private String save;
    private String saveActivate;
    private String saveUse;
    private String saveImageCard;
    private String cancel;
    private String rotateRight;
    private String rotateLeft;

    
    public ExternalFilesSaveImageCommand() {
    }

    
    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getSaveActivate() {
        return saveActivate;
    }

    public void setSaveActivate(String saveActivate) {
        this.saveActivate = saveActivate;
    }

    public String getSaveImageCard() {
        return saveImageCard;
    }

    public void setSaveImageCard(String saveImageCard) {
        this.saveImageCard = saveImageCard;
    }

    public String getSaveUse() {
        return saveUse;
    }

    public void setSaveUse(String saveUse) {
        this.saveUse = saveUse;
    }

    public String getRotateRight() {
        return rotateRight;
    }

    public void setRotateRight(String rotateRight) {
        this.rotateRight = rotateRight;
    }

    public String getRotateLeft() {
        return rotateLeft;
    }

    public void setRotateLeft(String rotateLeft) {
        this.rotateLeft = rotateLeft;
    }
}
