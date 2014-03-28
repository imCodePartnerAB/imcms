package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.Loop;
import imcode.server.document.textdocument.MenuDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies loop in a text document.
 */
public class TextDocLoopContainer {

    public static TextDocLoopContainer of(VersionRef versionRef, int loopNo, Loop loop) {
        return new TextDocLoopContainer(versionRef, loopNo, loop);
    }

    private final VersionRef versionRef;
    private final int loopNo;
    private final Loop loop;

    public TextDocLoopContainer(VersionRef versionRef, int loopNo, Loop loop) {
        this.versionRef = Objects.requireNonNull(versionRef);
        this.loop = Objects.requireNonNull(loop);
        this.loopNo = loopNo;
    }

    public int getLoopNo() {
        return loopNo;
    }

    public Loop getLoop() {
        return loop;
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

