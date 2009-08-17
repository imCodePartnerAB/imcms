package com.imcode.imcms.web.admin;

import imcode.server.Imcms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Cache controller.
 */
@Controller
public class CacheController {
	
	/**
	 * Cached documents
	 */
	@RequestMapping(value="/cache/info.html")
	public String cacheInfo() {
		return "forward:/WEB-INF/admin/cache/DocumentsCache.groovy";
	}
	
	/**
	 * Cached documents
	 */
	@RequestMapping(value="/cache/clear.html")
	public String clearCache() {
		Imcms.getServices().getDocumentMapper().getCachingDocumentGetter().clearCache();
		return "forward:/WEB-INF/admin/cache/DocumentsCache.groovy";
	}	
}