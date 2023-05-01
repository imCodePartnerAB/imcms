package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.container.TextDocImagesContainer;

@Deprecated
public class ImageEditResult {

    private boolean shareImages;
    private TextDocImagesContainer editedImages;
    private TextDocImagesContainer origImages;

    public ImageEditResult(boolean shareImages, TextDocImagesContainer origImages, TextDocImagesContainer editedImages) {
        this.shareImages = shareImages;
        this.origImages = origImages;
        this.editedImages = editedImages;
    }


    public TextDocImagesContainer getEditedImages() {
        return editedImages;
    }

    public void setEditedImages(TextDocImagesContainer editedImages) {
        this.editedImages = editedImages;
    }

    public TextDocImagesContainer getOrigImages() {
        return origImages;
    }

    public void setOrigImages(TextDocImagesContainer origImages) {
        this.origImages = origImages;
    }

    public boolean isShareImages() {
        return shareImages;
    }

    public void setShareImages(boolean shareImages) {
        this.shareImages = shareImages;
    }
}
