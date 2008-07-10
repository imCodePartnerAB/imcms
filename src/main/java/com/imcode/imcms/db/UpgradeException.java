package com.imcode.imcms.db;

public class UpgradeException extends Exception {

    public UpgradeException(Exception re) {
        super(re) ;
    }

    protected UpgradeException() {
    }
}
