package com.imcode.imcms.web.admin;

/**
 * Application info controller.
 */
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Profile controller.
 */
@Controller
public class ProfileController {

	@RequestMapping(value="/profile.html")
	public String documentsCache() {
		throw new NotImplementedException();
	}
}