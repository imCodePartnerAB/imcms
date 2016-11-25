package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.textdocument.TextDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies texts sharing the same version and the slot in a document.
 */
public class TextDocTextsContainer extends TextDocObjectVersionedContainer {

    public static TextDocTextsContainer of(VersionRef versionRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        return new TextDocTextsContainer(versionRef, null, textNo, texts);
    }

    public static TextDocTextsContainer of(VersionRef versionRef, LoopEntryRef loopEntryRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        return new TextDocTextsContainer(versionRef, loopEntryRef, textNo, texts);
    }

    private final Map<DocumentLanguage, TextDomainObject> texts;

    public TextDocTextsContainer(VersionRef versionRef, LoopEntryRef loopEntryRef, int textNo, Map<DocumentLanguage, TextDomainObject> texts) {
        super(loopEntryRef, textNo, versionRef);
        this.texts = Objects.requireNonNull(texts);
    }

    public int getTextNo() {
        return getDomainObjectNo();
    }

    public Map<DocumentLanguage, TextDomainObject> getTexts() {
        return texts;
    }
}
