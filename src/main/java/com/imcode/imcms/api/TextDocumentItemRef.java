package com.imcode.imcms.api;

/**
 * Uniquely identifies an item in the text document - text, image or loop.
 * @param <T>
 */
public class TextDocumentItemRef<T> {

    public static <T> TextDocumentItemRef<T> of(DocRef docRef, int itemNo, T item) {
        return new TextDocumentItemRef<>(docRef, itemNo, item);
    }

    private final DocRef docRef;
    private final int itemNo;
    private final T item;

    public TextDocumentItemRef(DocRef docRef, int itemNo, T item) {
        this.docRef = docRef;
        this.itemNo = itemNo;
        this.item = item;
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public int getItemNo() {
        return itemNo;
    }

    public T getItem() {
        return item;
    }
}
