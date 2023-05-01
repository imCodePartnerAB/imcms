package com.imcode.imcms.mapping.container;

import com.imcode.imcms.model.Language;
import imcode.server.document.textdocument.TextDomainObject;

import java.util.Map;
import java.util.Objects;

/**
 * Uniquely identifies texts sharing the same version and the slot in a document.
 */
public class TextDocTextsContainer extends TextDocObjectVersionedContainer {

    private final Map<Language, TextDomainObject> texts;

    public TextDocTextsContainer(VersionRef versionRef, LoopEntryRef loopEntryRef, int textNo, Map<Language, TextDomainObject> texts) {
        super(loopEntryRef, textNo, versionRef);
        this.texts = Objects.requireNonNull(texts);
    }

    public static TextDocTextsContainer of(VersionRef versionRef, int textNo, Map<Language, TextDomainObject> texts) {
        return new TextDocTextsContainer(versionRef, null, textNo, texts);
    }

    public static TextDocTextsContainer of(VersionRef versionRef, LoopEntryRef loopEntryRef, int textNo, Map<Language, TextDomainObject> texts) {
        return new TextDocTextsContainer(versionRef, loopEntryRef, textNo, texts);
    }

    public int getTextNo() {
        return getDomainObjectNo();
    }

    public Map<Language, TextDomainObject> getTexts() {
        return texts;
    }
}
