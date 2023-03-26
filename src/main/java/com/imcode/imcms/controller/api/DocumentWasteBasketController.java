package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.domain.service.DocumentWasteBasketService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/document/basket")
public class DocumentWasteBasketController {

    private final DocumentWasteBasketService documentWasteBasketService;
    private final DelegatingByTypeDocumentService documentService;

    DocumentWasteBasketController(DocumentWasteBasketService documentWasteBasketService,
                                  DelegatingByTypeDocumentService documentService) {
        this.documentWasteBasketService = documentWasteBasketService;
        this.documentService = documentService;
    }

    @GetMapping
    @CheckAccess
    public List<Document> get() {
        return documentWasteBasketService.getAllIdsFromWasteBasket().stream()
                .map(documentService::get)
                .collect(Collectors.toList());
    }

    @PostMapping("/{docId}")
    @CheckAccess
    public Document putToWasteBasket(@PathVariable Integer docId) {
        documentWasteBasketService.putToWasteBasket(docId);
        return documentService.get(docId);
    }

    @PostMapping
    @CheckAccess
    public void putToWasteBasket(@RequestBody List<Integer> ids) {
        documentWasteBasketService.putToWasteBasket(ids);
    }

    @DeleteMapping("/{docId}")
    @CheckAccess
    public void pullFromWasteBasket(@PathVariable Integer docId) {
        documentWasteBasketService.pullFromWasteBasket(docId);
    }

    @DeleteMapping
    @CheckAccess
    public void pullFromWasteBasket(@RequestBody List<Integer> ids) {
        documentWasteBasketService.pullFromWasteBasket(ids);
    }

}
