package com.imcode.imcms.api;

/**
 * Usually thrown when saving a {@link Document}. Signals that a document was assigned more categories of a
 * {@link CategoryType} that that category type's maximum number of choices allows.
 * @author kreiger
 */
public class MaxCategoriesOfTypeExceededException extends SaveException {

    MaxCategoriesOfTypeExceededException(Throwable cause) {
        super(cause) ;
    }
}
