package com.imcode.imcms.mapping.container;

import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies an text document image.
 */
public class TextDocImageContainer {

    public static TextDocImageContainer of(DocRef docRef, int imageNo, ImageDomainObject image) {
        return new TextDocImageContainer(docRef, null, imageNo, image);
    }

    public static TextDocImageContainer of(DocRef docRef, LoopEntryRef loopEntryRef, int imageNo, ImageDomainObject image) {
        return new TextDocImageContainer(docRef, loopEntryRef, imageNo, image);
    }

    private final DocRef docRef;
    private final int imageNo;
    private final LoopEntryRef loopEntryRef;
    private final ImageDomainObject image;

    public TextDocImageContainer(DocRef docRef, LoopEntryRef loopEntryRef, int imageNo, ImageDomainObject image) {
        Objects.requireNonNull(docRef);
        Objects.requireNonNull(image);

        this.docRef = docRef;
        this.loopEntryRef = loopEntryRef;
        this.imageNo = imageNo;
        this.image = image;
    }

    public int getImageNo() {
        return imageNo;
    }

    public ImageDomainObject getImage() {
        return image;
    }

    public boolean isLoopEntryItem() {
        return loopEntryRef != null;
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public LoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }

    public DocVersionRef getDocVersionRef() {
        return docRef.getDocVersionRef();
    }

    public int getDocId() {
        return docRef.getDocId();
    }

    public int getDocVersionNo() {
        return docRef.getDocVersionNo();
    }

    public String getDocLanguageCode() {
        return docRef.getDocLanguageCode();
    }
}

