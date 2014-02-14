package com.imcode.imcms.api;

/**
 * Uniquely identifies an item in the text document - text, image or loop.
 * @param <T>
 */
public class TextDocumentLoopItemRef<T> {

    public static <T> TextDocumentLoopItemRef<T> of(DocVersionRef docVersionRef, LoopItemRef loopItemRef, T item) {
        return new TextDocumentLoopItemRef<>(docVersionRef, loopItemRef, item);
    }

    private final DocVersionRef docVersionRef;
    private final LoopItemRef loopItemRef;
    private final T item;

    public TextDocumentLoopItemRef(DocVersionRef docVersionRef, LoopItemRef loopItemRef, T item) {
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
