package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/document-cache")
public class PublicDocumentCacheController {

	private final DocumentsCache documentsCache;

	public PublicDocumentCacheController(DocumentsCache documentsCache) {
		this.documentsCache = documentsCache;
	}

	@GetMapping("/invalidate")
	@CheckAccess(docPermission = AccessContentType.DOC_INFO)
	public void invalidateCache(@RequestParam int docId, @RequestParam(required = false) Collection<String> aliases) {
		documentsCache.invalidateDoc(docId, aliases);
	}
}
