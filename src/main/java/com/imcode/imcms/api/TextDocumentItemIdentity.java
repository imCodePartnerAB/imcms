package com.imcode.imcms.api;

/**
 * Used to uniquely identify an item in the text document - text, image and content loop.
 * @param <T>
 */
public class TextDocumentItemIdentity<T> {

    public static <T> TextDocumentItemIdentity<T> of(DocumentIdentity documentIdentity, int itemNo, T item) {
        return new TextDocumentItemIdentity<>(documentIdentity, itemNo, item);
    }

    private final DocumentIdentity documentIdentity;
    private final int itemNo;
    private final T item;

    public TextDocumentItemIdentity(DocumentIdentity documentIdentity, int itemNo, T item) {
        this.documentIdentity = documentIdentity;
        this.itemNo = itemNo;
        this.item = item;
    }

    public DocumentIdentity getDocumentIdentity() {
        return documentIdentity;
    }

    public int getItemNo() {
        return itemNo;
    }

    public T getItem() {
        return item;
    }
}
