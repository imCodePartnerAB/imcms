package com.imcode.imcms.mapping.container;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 22.11.16.
 */
abstract class TextDocContainer implements Container {
    private final LoopEntryRef loopEntryRef;
    private final int domainObjectNo;

    TextDocContainer(LoopEntryRef loopEntryRef, int domainObjectNo) {
        this.loopEntryRef = loopEntryRef;
        this.domainObjectNo = domainObjectNo;
    }

    public boolean isLoopEntryItem() {
        return loopEntryRef != null;
    }

    public LoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }

    protected int getDomainObjectNo() {
        return domainObjectNo;
    }
}
