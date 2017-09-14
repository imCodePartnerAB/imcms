package com.imcode.imcms.mapping.container;

import imcode.server.document.textdocument.TextDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies text in a text document.
 */
public class TextDocTextContainer extends TextDocObjectContainer {

    private final TextDomainObject text;

    public TextDocTextContainer(DocRef docRef, LoopEntryRef loopEntryRef, int textNo, TextDomainObject text) {
        super(docRef, loopEntryRef, textNo);
        this.text = Objects.requireNonNull(text);
    }

    public static TextDocTextContainer of(DocRef docRef, int textNo, TextDomainObject text) {
        return new TextDocTextContainer(docRef, null, textNo, text);
    }

    public static TextDocTextContainer of(DocRef docRef, LoopEntryRef loopEntryRef, int textNo, TextDomainObject text) {
        return new TextDocTextContainer(docRef, loopEntryRef, textNo, text);
    }

    public int getTextNo() {
        return getDomainObjectNo();
    }

    public TextDomainObject getText() {
        return text;
    }
}
