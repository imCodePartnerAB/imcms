package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.textdocument.TextDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies texts sharing the same version and the slot in a document.
 */
public class TextDocTextsContainer {

    public static TextDocTextsContainer of(DocVersionRef docVersionRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        return new TextDocTextsContainer(docVersionRef, null, textNo, texts);
    }

    public static TextDocTextsContainer of(DocVersionRef docVersionRef, LoopEntryRef loopEntryRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        return new TextDocTextsContainer(docVersionRef, loopEntryRef, textNo, texts);
    }

    private final DocVersionRef docVersionRef;
    private final int textNo;
    private final LoopEntryRef loopEntryRef;
    private final Map<DocumentLanguage, TextDomainObject> texts;

    public TextDocTextsContainer(DocVersionRef docVersionRef, LoopEntryRef loopEntryRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        Objects.requireNonNull(docVersionRef);
        Objects.requireNonNull(texts);

        this.docVersionRef = docVersionRef;
        this.loopEntryRef = loopEntryRef;
        this.textNo = textNo;
        this.texts = texts;
    }

    public int getTextNo() {
        return textNo;
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

    public Map<DocumentLanguage, TextDomainObject> getTexts() {
        return texts;
    }
}

