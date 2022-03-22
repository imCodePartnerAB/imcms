package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TextValidationResult;
import com.imcode.imcms.domain.dto.ValidationData;
import com.imcode.imcms.domain.service.TextValidationService;
import org.springframework.stereotype.Service;

/**
 * {@link TextValidationService} implementation with w3c text validation.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Service
class W3cTextValidationService implements TextValidationService {

	@Override
	public TextValidationResult validateText(String content) {
		// this wrapping is necessary because validator checks full page, not the piece of html.
		final String contentWrapper = "<!DOCTYPE html>\n"
				+ "<html lang=\"en\">\n"
				+ "  <head>\n"
				+ "    <meta charset=\"utf-8\">\n"
				+ "    <title>ImCMS HTML validation</title>\n"
				+ "    <meta name=\"description\" content=\"ImCMS HTML validation\">\n"
				+ "  </head>\n"
				+ "  <body>%s</body>\n"
				+ "</html>";

		content = String.format(contentWrapper, content);

		ValidationData data = new W3cValidatorWrapper().validateText(content);
		return new TextValidationResult(data);
	}
}
