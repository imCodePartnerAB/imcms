package com.imcode.imcms.api;

public class TextDocItemRef<T> {

    public static <T> TextDocItemRef<T> of(DocRef docRef, int itemNo, T item) {
        return new TextDocItemRef<>(docRef, itemNo, item);
    }

    private final DocRef docRef;
    private final int itemNo;
    private final T item;

    public TextDocItemRef(DocRef docRef, int itemNo, T item) {
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
