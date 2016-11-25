package com.imcode.imcms.mapping.container;

import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies an text document image.
 */
public class TextDocImageContainer extends TextDocObjectContainer {

    public static TextDocImageContainer of(DocRef docRef, int imageNo, ImageDomainObject image) {
        return new TextDocImageContainer(docRef, null, imageNo, image);
    }

    public static TextDocImageContainer of(DocRef docRef, LoopEntryRef loopEntryRef, int imageNo, ImageDomainObject image) {
        return new TextDocImageContainer(docRef, loopEntryRef, imageNo, image);
    }

    private final ImageDomainObject image;

    public TextDocImageContainer(DocRef docRef, LoopEntryRef loopEntryRef, int imageNo, ImageDomainObject image) {
        super(docRef, loopEntryRef, imageNo);
        this.image = Objects.requireNonNull(image);
    }

    public int getImageNo() {
        return getDomainObjectNo();
    }

    public ImageDomainObject getImage() {
        return image;
    }
}
