package com.imcode.imcms.api;

/**
 * Uniquely identifies an item in the text document - text, image or loop.
 * @param <T>
 */
public class TextDocumentLoopItemRef<T> {

    public static <T> TextDocumentLoopItemRef<T> of(DocRef docRef, int itemNo, LoopItemRef loopItemRef, T item) {
        return new TextDocumentLoopItemRef<>(docRef, itemNo, loopItemRef, item);
    }

    private final DocRef docRef;
    private final int itemNo;
    private final LoopItemRef loopItemRef;
    private final T item;

    public TextDocumentLoopItemRef(DocRef docRef, int itemNo, LoopItemRef loopItemRef, T item) {
        this.docRef = docRef;
        this.itemNo = itemNo;
        this.loopItemRef = loopItemRef;
        this.item = item;
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public int getItemNo() {
        return itemNo;
    }

    public LoopItemRef getLoopItemRef() {
        return loopItemRef;
    }

    public T getItem() {
        return item;
    }
}
