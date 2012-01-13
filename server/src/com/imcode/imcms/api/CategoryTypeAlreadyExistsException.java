package com.imcode.imcms.api;

/**
 * Usually thrown when creating a {@link CategoryType}.
 * Signals that the new category type's name already belongs to another category type.
 */
public class CategoryTypeAlreadyExistsException extends AlreadyExistsException {
    public CategoryTypeAlreadyExistsException( String message ) {
        super( message, null );
    }
}
