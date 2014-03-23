package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.textdocument.TextDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies texts sharing the same version and the slot in a document.
 */
public class TextDocTextsContainer {

    public static TextDocTextsContainer of(VersionRef versionRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        return new TextDocTextsContainer(versionRef, null, textNo, texts);
    }

    public static TextDocTextsContainer of(VersionRef versionRef, LoopEntryRef loopEntryRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        return new TextDocTextsContainer(versionRef, loopEntryRef, textNo, texts);
    }

    private final VersionRef versionRef;
    private final int textNo;
    private final LoopEntryRef loopEntryRef;
    private final Map<DocumentLanguage, TextDomainObject> texts;

    public TextDocTextsContainer(VersionRef versionRef, LoopEntryRef loopEntryRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        this.versionRef = Objects.requireNonNull(versionRef);
        this.texts = Objects.requireNonNull(texts);
        this.loopEntryRef = loopEntryRef;
        this.textNo = textNo;
    }

    public int getTextNo() {
        return textNo;
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

    public Map<DocumentLanguage, TextDomainObject> getTexts() {
        return texts;
    }
}

