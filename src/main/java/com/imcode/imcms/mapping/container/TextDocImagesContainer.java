package com.imcode.imcms.mapping.container;

import com.imcode.imcms.model.Language;
import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies images sharing the same version and the slot in a document.
 */
public class TextDocImagesContainer extends TextDocObjectVersionedContainer {

    private final Map<Language, ImageDomainObject> images;

    public TextDocImagesContainer(VersionRef versionRef, LoopEntryRef loopEntryRef, int imageNo, Map<Language, ImageDomainObject> images) {
        super(loopEntryRef, imageNo, versionRef);
        this.images = Objects.requireNonNull(images);
    }

    public static TextDocImagesContainer of(VersionRef versionRef, int imageNo, Map<Language, ImageDomainObject> images) {
        return new TextDocImagesContainer(versionRef, null, imageNo, images);
    }

    public static TextDocImagesContainer of(VersionRef versionRef, LoopEntryRef loopEntryRef, int imageNo, Map<Language, ImageDomainObject> images) {
        return new TextDocImagesContainer(versionRef, loopEntryRef, imageNo, images);
    }

    public Map<Language, ImageDomainObject> getImages() {
        return images;
    }

    public int getImageNo() {
        return getDomainObjectNo();
    }
}
