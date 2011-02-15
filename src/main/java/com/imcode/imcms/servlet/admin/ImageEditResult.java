package com.imcode.imcms.servlet.admin;

import imcode.server.document.textdocument.ImageDomainObject;
import java.util.List;

public class ImageEditResult {
    private boolean shareImages;
    private List<ImageDomainObject> origImages;
    private List<ImageDomainObject> editedImages;


    public ImageEditResult() {
    }

    public ImageEditResult(boolean shareImages, List<ImageDomainObject> origImages, List<ImageDomainObject> editedImages) {
        this.shareImages = shareImages;
        this.origImages = origImages;
        this.editedImages = editedImages;
    }

    
    public List<ImageDomainObject> getEditedImages() {
        return editedImages;
    }

    public void setEditedImages(List<ImageDomainObject> editedImages) {
        this.editedImages = editedImages;
    }

    public List<ImageDomainObject> getOrigImages() {
        return origImages;
    }

    public void setOrigImages(List<ImageDomainObject> origImages) {
        this.origImages = origImages;
    }

    public boolean isShareImages() {
        return shareImages;
    }

    public void setShareImages(boolean shareImages) {
        this.shareImages = shareImages;
    }
}
