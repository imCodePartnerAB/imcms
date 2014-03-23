package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies images sharing the same version and the slot in a document.
 */
public class TextDocImagesContainer {

    public static TextDocImagesContainer of(VersionRef versionRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        return new TextDocImagesContainer(versionRef, null, imageNo, images);
    }

    public static TextDocImagesContainer of(VersionRef versionRef, LoopEntryRef loopEntryRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        return new TextDocImagesContainer(versionRef, loopEntryRef, imageNo, images);
    }

    private final VersionRef versionRef;
    private final int imageNo;
    private final LoopEntryRef loopEntryRef;
    private final Map<DocumentLanguage, ImageDomainObject> images;

    public TextDocImagesContainer(VersionRef versionRef, LoopEntryRef loopEntryRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        this.versionRef = Objects.requireNonNull(versionRef);
        this.images = Objects.requireNonNull(images);
        this.loopEntryRef = loopEntryRef;
        this.imageNo = imageNo;
    }

    public VersionRef getVersionRef() {
        return versionRef;
    }

    public boolean isLoopEntryItem() {
        return loopEntryRef != null;
    }

    public LoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }

    public int getDocId() {
        return versionRef.getDocId();
    }

    public int getVersionNo() {
        return versionRef.getNo();
    }

    public Map<DocumentLanguage, ImageDomainObject> getImages() {
        return images;
    }

    public int getImageNo() {
        return imageNo;
    }
}

