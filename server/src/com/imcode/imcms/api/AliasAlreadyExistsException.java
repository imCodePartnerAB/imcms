package com.imcode.imcms.api;

/**
 * Usually thrown when saving a {@link Document}. Signals that there's already another document in imcms with an alias that the document that's being saved has.
 */
public class AliasAlreadyExistsException extends SaveException {
    public AliasAlreadyExistsException(Throwable cause) {
        super("Alias already exists.", cause);
    }
}
