package com.imcode.imcms.api;

/**
 * @since 2.0
 */
public class AlreadyExistsException extends SaveException {

    private static final long serialVersionUID = -4956711037528286052L;

    public AlreadyExistsException(Throwable e) {
        super(e);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}
