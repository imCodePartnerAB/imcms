package com.imcode.imcms.api;

/**
 * Created by IntelliJ IDEA.
 * User: Hasse
 * Date: 2004-apr-21
 * Time: 10:56:31
 * To change this template use File | Settings | File Templates.
 */
public class CategoryAlreadyExistsException extends Exception {
    public CategoryAlreadyExistsException( String message ) {
        super( message );
    }
}
