package com.imcode.imcms.components.exception;

public class CompressionImageException extends Exception{

    public CompressionImageException(){
        super();
    }

    public CompressionImageException(String message){
        super(message);
    }

    public CompressionImageException(String message, Throwable cause) {
        super( message, cause );
    }

    public CompressionImageException(Throwable cause) {
        super( cause );
    }

}
