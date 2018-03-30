package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TextValidationResult;
import com.imcode.imcms.domain.service.TextValidationService;
import com.jcabi.w3c.Defect;
import com.jcabi.w3c.ValidationResponse;
import com.jcabi.w3c.ValidatorBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * {@link TextValidationService} implementation with w3c text validation.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Service
class W3cTextValidationService implements TextValidationService {

    /**
     * {@inheritDoc}
     *
     * @param content Text, that should be validated by W3C
     * @see ValidatorBuilder
     * @see ValidationResponse
     * @see com.jcabi.w3c.Validator
     * @see Defect
     */
    @Override
    public TextValidationResult validateText(String content) throws IOException {
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

        final ValidationResponse response = new ValidatorBuilder().html().validate(content);

        return new TextValidationResult(response);
    }
}
