package com.imcode.imcms.mapping.container;

import java.util.Objects;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.11.16.
 */
public class TextDocObjectContainer extends TextDocContainer implements LanguageContainer {
    private final DocRef docRef;

    TextDocObjectContainer(DocRef docRef, LoopEntryRef loopEntryRef, int domainObjectNo) {
        super(loopEntryRef, domainObjectNo);
        this.docRef = Objects.requireNonNull(docRef);
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public VersionRef getDocVersionRef() {
        return docRef.getVersionRef();
    }

    public int getDocId() {
        return docRef.getId();
    }

    public int getVersionNo() {
        return docRef.getVersionNo();
    }

    public String getLanguageCode() {
        return docRef.getLanguageCode();
    }
}
