package com.imcode.imcms.domain.exception;

import static java.lang.String.format;

public class LanguageNotAvailableException extends RuntimeException {

    public LanguageNotAvailableException(String code) {
        super(format("Language with code \'%s\' is not available!", code));
    }
}
