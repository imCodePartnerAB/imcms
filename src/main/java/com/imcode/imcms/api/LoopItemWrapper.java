package com.imcode.imcms.api;

/**
 * Uniquely identifies an item in the text document loop - text, image.
 * @param <T>
 */
public class LoopItemWrapper<T> {

    public static <T> LoopItemWrapper<T> of(DocVersionRef docVersionRef, LoopItemRef loopItemRef, T item) {
        return new LoopItemWrapper<>(docVersionRef, loopItemRef, item);
    }

    private final DocVersionRef docVersionRef;
    private final LoopItemRef loopItemRef;
    private final T item;

    public LoopItemWrapper(DocVersionRef docVersionRef, LoopItemRef loopItemRef, T item) {
        this.docVersionRef = docVersionRef;
        this.loopItemRef = loopItemRef;
        this.item = item;
    }

    public DocVersionRef getDocVersionRef() {
        return docVersionRef;
    }

    public LoopItemRef getLoopItemRef() {
        return loopItemRef;
    }

    public T getItem() {
        return item;
    }
}
