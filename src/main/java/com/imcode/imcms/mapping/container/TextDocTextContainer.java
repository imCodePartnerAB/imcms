package com.imcode.imcms.mapping.container;

import imcode.server.document.textdocument.TextDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies text in a text document.
 */
public class TextDocTextContainer {

    public static TextDocTextContainer of(DocRef docRef, int textNo, TextDomainObject text) {
        return new TextDocTextContainer(docRef, null, textNo, text);
    }

    public static TextDocTextContainer of(DocRef docRef, LoopEntryRef loopEntryRef, int textNo, TextDomainObject text) {
        return new TextDocTextContainer(docRef, loopEntryRef, textNo, text);
    }

    private final DocRef docRef;
    private final int textNo;
    private final LoopEntryRef loopEntryRef;
    private final TextDomainObject text;

    public TextDocTextContainer(DocRef docRef, LoopEntryRef loopEntryRef, int textNo, TextDomainObject text) {
        Objects.requireNonNull(docRef);
        Objects.requireNonNull(text);

        this.docRef = docRef;
        this.loopEntryRef = loopEntryRef;
        this.textNo = textNo;
        this.text = text;
    }

    public int getTextNo() {
        return textNo;
    }

    public TextDomainObject getText() {
        return text;
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public boolean isLoopEntryItem() {
        return loopEntryRef != null;
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

