package com.imcode.imcms.api;

/**
 * Created by IntelliJ IDEA.
 * User: Hasse
 * Date: 2004-apr-21
 * Time: 10:53:31
 * To change this template use File | Settings | File Templates.
 */
public class CategoryTypeAlreadyExistsException extends Exception {
    public CategoryTypeAlreadyExistsException( String message ) {
        super( message );
    }
}
