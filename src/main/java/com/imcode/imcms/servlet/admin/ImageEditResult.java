package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.container.TextDocImageContainer;

import java.util.List;

public class ImageEditResult {
    private boolean shareImages;
    private List<TextDocImageContainer> editedImages;
    private List<TextDocImageContainer> origImages;


    public ImageEditResult() {
    }

    public ImageEditResult(boolean shareImages, List<TextDocImageContainer> origImages, List<TextDocImageContainer> editedImages) {
        this.shareImages = shareImages;
        this.origImages = origImages;
        this.editedImages = editedImages;
    }


    public List<TextDocImageContainer> getEditedImages() {
        return editedImages;
    }

    public void setEditedImages(List<TextDocImageContainer> editedImages) {
        this.editedImages = editedImages;
    }

    public List<TextDocImageContainer> getOrigImages() {
        return origImages;
    }

    public void setOrigImages(List<TextDocImageContainer> origImages) {
        this.origImages = origImages;
    }

    public boolean isShareImages() {
        return shareImages;
    }

    public void setShareImages(boolean shareImages) {
        this.shareImages = shareImages;
    }
}
