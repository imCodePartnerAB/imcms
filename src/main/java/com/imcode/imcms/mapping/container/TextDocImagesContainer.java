package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies images sharing the same version and the slot in a document.
 */
public class TextDocImagesContainer {

    public static TextDocImagesContainer of(DocVersionRef docVersionRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        return new TextDocImagesContainer(docVersionRef, null, imageNo, images);
    }

    public static TextDocImagesContainer of(DocVersionRef docVersionRef, LoopEntryRef loopEntryRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        return new TextDocImagesContainer(docVersionRef, loopEntryRef, imageNo, images);
    }

    private final DocVersionRef docVersionRef;
    private final int imageNo;
    private final LoopEntryRef loopEntryRef;
    private final Map<DocumentLanguage, ImageDomainObject> images;

    public TextDocImagesContainer(DocVersionRef docVersionRef, LoopEntryRef loopEntryRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        Objects.requireNonNull(docVersionRef);
        Objects.requireNonNull(images);

        this.docVersionRef = docVersionRef;
        this.loopEntryRef = loopEntryRef;
        this.imageNo = imageNo;
        this.images = images;
    }

    public DocVersionRef getDocVersionRef() {
        return docVersionRef;
    }

    public boolean isLoopEntryItem() {
        return loopEntryRef != null;
    }

    public LoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }
    public int getDocId() {
        return docVersionRef.getDocId();
    }

    public int getDocVersionNo() {
        return docVersionRef.getDocVersionNo();
    }

    public Map<DocumentLanguage, ImageDomainObject> getImages() {
        return images;
    }

    public int getImageNo() {
        return imageNo;
    }
}

