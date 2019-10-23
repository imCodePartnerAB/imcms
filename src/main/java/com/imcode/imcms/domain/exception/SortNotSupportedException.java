package com.imcode.imcms.domain.exception;

public class SortNotSupportedException extends RuntimeException {
    private static final long serialVersionUID = 3599379164719922771L;

    public SortNotSupportedException(String message) {
        super(message);
    }
}
