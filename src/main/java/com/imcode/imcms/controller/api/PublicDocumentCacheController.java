package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document-cache")
public class PublicDocumentCacheController {

    private final DocumentsCache documentsCache;

    public PublicDocumentCacheController(DocumentsCache documentsCache) {
        this.documentsCache = documentsCache;
    }


    @GetMapping("/invalidate")
    public void invalidateCache(@RequestParam int docId, @RequestParam(required = false) String alias) {
        documentsCache.invalidateDoc(docId, alias);
    }
}
