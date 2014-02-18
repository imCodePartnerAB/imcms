package com.imcode.imcms.api;

/**
 * Uniquely identifies an item in the text document - text, image or loop.
 * @param <T>
 */
public class TextDocumentItemWrapper<T> {

    public static <T> TextDocumentItemWrapper<T> of(DocRef docRef, int itemNo, T item) {
        return new TextDocumentItemWrapper<>(docRef, itemNo, item);
    }

    private final DocRef docRef;
    private final int itemNo;
    private final T item;

    public TextDocumentItemWrapper(DocRef docRef, int itemNo, T item) {
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
