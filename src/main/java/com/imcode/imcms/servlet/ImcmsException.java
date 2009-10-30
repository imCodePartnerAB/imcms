package com.imcode.imcms.servlet;

/**
 * 
 */
public class ImcmsException extends RuntimeException {
    public ImcmsException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImcmsException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImcmsException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImcmsException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
