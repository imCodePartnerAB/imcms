package com.imcode.imcms.web.admin;

/**
 * Application info controller.
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Document search controller.
 */
@Controller
public class InfoController {

	/**
	 * Cached documents - for test purposes.
	 */
	@RequestMapping(value="/cache.html")
	public String documentsCache() {
		return "forward:/WEB-INF/admin/info/DocumentsCache.groovy";
	}
}