package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.DocumentWasteBasket;

import java.util.List;

public interface DocumentWasteBasketService {

    List<Integer> getAllIdsFromWasteBasket();

    List<DocumentWasteBasket> getAllFromWasteBasket();

    boolean isDocumentInWasteBasket(int docId);

    void putToWasteBasket(int docId);

    void putToWasteBasket(List<Integer> ids);

    void pullFromWasteBasket(int docId);

    void pullFromWasteBasket(List<Integer> docIds);

}
