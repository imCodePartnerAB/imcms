package com.imcode.imcms.mapping.container;

import com.imcode.imcms.api.Loop;
import imcode.server.document.textdocument.MenuDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies loop in a text document.
 */
public class TextDocLoopContainer extends TextDocVersionedContainer {

    public static TextDocLoopContainer of(VersionRef versionRef, int loopNo, Loop loop) {
        return new TextDocLoopContainer(versionRef, loopNo, loop);
    }

    private final int loopNo;
    private final Loop loop;

    public TextDocLoopContainer(VersionRef versionRef, int loopNo, Loop loop) {
        super(versionRef);
        this.loop = Objects.requireNonNull(loop);
        this.loopNo = loopNo;
    }

    public int getLoopNo() {
        return loopNo;
    }

    public Loop getLoop() {
        return loop;
    }
}
