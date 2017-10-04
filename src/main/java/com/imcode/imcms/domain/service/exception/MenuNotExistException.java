package com.imcode.imcms.domain.service.exception;

public class MenuNotExistException extends RuntimeException {

    public MenuNotExistException(int menuNo, int documentId) {
        super(String.format("Menu with no = %d and documentId = %d does not exist!", menuNo, documentId));
    }

}
