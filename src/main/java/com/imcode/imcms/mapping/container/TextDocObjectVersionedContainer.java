package com.imcode.imcms.mapping.container;

import java.util.Objects;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.11.16.
 */
class TextDocObjectVersionedContainer extends TextDocContainer {
    private final VersionRef versionRef;

    TextDocObjectVersionedContainer(LoopEntryRef loopEntryRef, int domainObjectNo, VersionRef versionRef) {
        super(loopEntryRef, domainObjectNo);
        this.versionRef = Objects.requireNonNull(versionRef);
    }

    public VersionRef getVersionRef() {
        return versionRef;
    }

    public int getDocId() {
        return versionRef.getDocId();
    }

    public int getVersionNo() {
        return versionRef.getNo();
    }
}
