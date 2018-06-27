package com.imcode.imcms.api;

/**
 * @since 2.0
 */
public class SaveException extends RuntimeException {

    private static final long serialVersionUID = -2223009108770202379L;

    public SaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveException(Throwable cause) {
        super(cause);
    }

    public SaveException(String message) {
        super(message);
    }
}
