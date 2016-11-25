package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies images sharing the same version and the slot in a document.
 */
public class TextDocImagesContainer extends TextDocObjectVersionedContainer {

    public static TextDocImagesContainer of(VersionRef versionRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        return new TextDocImagesContainer(versionRef, null, imageNo, images);
    }

    public static TextDocImagesContainer of(VersionRef versionRef, LoopEntryRef loopEntryRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        return new TextDocImagesContainer(versionRef, loopEntryRef, imageNo, images);
    }

    private final Map<DocumentLanguage, ImageDomainObject> images;

    public TextDocImagesContainer(VersionRef versionRef, LoopEntryRef loopEntryRef, int imageNo, Map<DocumentLanguage, ImageDomainObject> images) {
        super(loopEntryRef, imageNo, versionRef);
        this.images = Objects.requireNonNull(images);
    }

    public Map<DocumentLanguage, ImageDomainObject> getImages() {
        return images;
    }

    public int getImageNo() {
        return getDomainObjectNo();
    }
}
