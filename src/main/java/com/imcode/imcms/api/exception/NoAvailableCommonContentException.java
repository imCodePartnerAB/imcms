package com.imcode.imcms.api.exception;

public class NoAvailableCommonContentException extends RuntimeException {
    public NoAvailableCommonContentException() {
    }

    public NoAvailableCommonContentException(String message) {
        super(message);
    }

    public NoAvailableCommonContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAvailableCommonContentException(Throwable cause) {
        super(cause);
    }

    public NoAvailableCommonContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
