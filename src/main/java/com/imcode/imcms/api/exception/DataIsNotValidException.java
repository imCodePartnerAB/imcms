package com.imcode.imcms.api.exception;

public class DataIsNotValidException extends RuntimeException {

    public DataIsNotValidException() {
    }

    public DataIsNotValidException(String message) {
        super(message);
    }
}
