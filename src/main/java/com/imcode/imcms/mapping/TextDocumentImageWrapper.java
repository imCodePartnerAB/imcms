package com.imcode.imcms.mapping;

import imcode.server.document.textdocument.ImageDomainObject;

/**
 * Uniquely identifies an text document image.
 */
public class TextDocumentImageWrapper {

    public static TextDocumentImageWrapper of(DocRef docRef, int imageNo, ImageDomainObject image) {
        return new TextDocumentImageWrapper(docRef, null, imageNo, image);
    }

    public static TextDocumentImageWrapper of(DocRef docRef, LoopEntryRef loopEntryRef, int imageNo, ImageDomainObject image) {
        return new TextDocumentImageWrapper(docRef, loopEntryRef, imageNo, image);
    }

    private final DocRef docRef;
    private final int imageNo;
    private final LoopEntryRef loopEntryRef;
    private final ImageDomainObject image;

    public TextDocumentImageWrapper(DocRef docRef, LoopEntryRef loopEntryRef, int imageNo, ImageDomainObject image) {
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

