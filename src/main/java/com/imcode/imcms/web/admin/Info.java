package com.imcode.imcms.web.admin;

/**
 * Application info controller.
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Info {

	/**
	 * Cached documents.
	 */
	@RequestMapping(value="/cache.html")
	public String documentsCache() {
		return "forward:/WEB-INF/groovy/DocumentsCache.groovy";
	}
}