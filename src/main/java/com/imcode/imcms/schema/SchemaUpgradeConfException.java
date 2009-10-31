package com.imcode.imcms.schema;

/**
 * 
 */
public class SchemaUpgradeConfException extends RuntimeException {
    
    public SchemaUpgradeConfException() {
    }

    public SchemaUpgradeConfException(String message) {
        super(message);
    }

    public SchemaUpgradeConfException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaUpgradeConfException(Throwable cause) {
        super(cause);
    }
}
