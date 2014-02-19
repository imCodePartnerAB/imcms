package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.api.TextDocumentItemWrapper;
import imcode.server.document.textdocument.ImageDomainObject;

import java.util.LinkedList;
import java.util.List;

public class ImageEditResult {
    private boolean shareImages;
    private List<TextDocumentItemWrapper<ImageDomainObject>> editedImages;
    private List<TextDocumentItemWrapper<ImageDomainObject>> origImages;


    public ImageEditResult() {
    }

    public ImageEditResult(boolean shareImages, List<TextDocumentItemWrapper<ImageDomainObject>> origImages, List<TextDocumentItemWrapper<ImageDomainObject>> editedImages) {
        this.shareImages = shareImages;
        this.origImages = origImages;
        this.editedImages = editedImages;
    }

    
    public List<TextDocumentItemWrapper<ImageDomainObject>> getEditedImages() {
        return editedImages;
    }

    public void setEditedImages(List<TextDocumentItemWrapper<ImageDomainObject>> editedImages) {
        this.editedImages = editedImages;
    }

    public List<TextDocumentItemWrapper<ImageDomainObject>> getOrigImages() {
        return origImages;
    }

    public void setOrigImages(List<TextDocumentItemWrapper<ImageDomainObject>> origImages) {
        this.origImages = origImages;
    }

    public boolean isShareImages() {
        return shareImages;
    }

    public void setShareImages(boolean shareImages) {
        this.shareImages = shareImages;
    }
}
