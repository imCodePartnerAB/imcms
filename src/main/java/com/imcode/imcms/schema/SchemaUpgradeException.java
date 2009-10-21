package com.imcode.imcms.schema;

import com.imcode.imcms.ImcmsException;

/**
 * Indicates schema upgrade exception.
 */
public class SchemaUpgradeException extends ImcmsException {

    public SchemaUpgradeException() {
    }

    public SchemaUpgradeException(String message) {
        super(message);
    }

    public SchemaUpgradeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaUpgradeException(Throwable cause) {
        super(cause);
    }
}