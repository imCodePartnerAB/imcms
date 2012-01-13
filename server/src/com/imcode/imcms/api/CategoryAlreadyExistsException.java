package com.imcode.imcms.api;

/**
 * Usually thrown when saving a {@link Category}. Signals that there's already a category under it's {@link CategoryType}
 * with the same name.
 */
public class CategoryAlreadyExistsException extends AlreadyExistsException {
    public CategoryAlreadyExistsException( String message ) {
        super( message, null );
    }
}
