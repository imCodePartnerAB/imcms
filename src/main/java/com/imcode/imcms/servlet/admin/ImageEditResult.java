package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.TextDocumentImageWrapper;

import java.util.List;

public class ImageEditResult {
    private boolean shareImages;
    private List<TextDocumentImageWrapper> editedImages;
    private List<TextDocumentImageWrapper> origImages;


    public ImageEditResult() {
    }

    public ImageEditResult(boolean shareImages, List<TextDocumentImageWrapper> origImages, List<TextDocumentImageWrapper> editedImages) {
        this.shareImages = shareImages;
        this.origImages = origImages;
        this.editedImages = editedImages;
    }


    public List<TextDocumentImageWrapper> getEditedImages() {
        return editedImages;
    }

    public void setEditedImages(List<TextDocumentImageWrapper> editedImages) {
        this.editedImages = editedImages;
    }

    public List<TextDocumentImageWrapper> getOrigImages() {
        return origImages;
    }

    public void setOrigImages(List<TextDocumentImageWrapper> origImages) {
        this.origImages = origImages;
    }

    public boolean isShareImages() {
        return shareImages;
    }

    public void setShareImages(boolean shareImages) {
        this.shareImages = shareImages;
    }
}
