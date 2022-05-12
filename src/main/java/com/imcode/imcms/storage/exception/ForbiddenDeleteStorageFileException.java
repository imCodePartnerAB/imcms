package com.imcode.imcms.storage.exception;

public class ForbiddenDeleteStorageFileException extends RuntimeException{

    public ForbiddenDeleteStorageFileException(){}

    public ForbiddenDeleteStorageFileException(String message){
        super(message);
    }
}
