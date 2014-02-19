package com.imcode.imcms.api;

/**
 * Uniquely identifies an item in the text document - text, image, menu, loop.
 * @param <T>
 */
public class TextDocumentItemWrapper<T> {

    public static <T> TextDocumentItemWrapper<T> of(DocRef docRef, int itemNo, T item) {
        return new TextDocumentItemWrapper<>(docRef, null, itemNo, item);
    }

    public static <T> TextDocumentItemWrapper<T> of(DocRef docRef, LoopEntryRef loopEntryRef, int itemNo, T item) {
        return new TextDocumentItemWrapper<>(docRef, loopEntryRef, itemNo, item);
    }

    private final DocRef docRef;
    private final int itemNo;
    private final LoopEntryRef loopEntryRef;
    private final T item;

    public TextDocumentItemWrapper(DocRef docRef, LoopEntryRef loopEntryRef, int itemNo, T item) {
        this.docRef = docRef;
        this.loopEntryRef = loopEntryRef;
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

    public LoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }

    public DocVersionRef getDocVersionRef() {
        return docRef.getDocVersionRef();
    }

    public int getDocId() {
        return docRef.getDocId();
    }

    public int getDocVersionNo() {
        return docRef.getDocVersionNo();
    }

    public String getDocLanguageCode() {
        return docRef.getDocLanguageCode();
    }
}

