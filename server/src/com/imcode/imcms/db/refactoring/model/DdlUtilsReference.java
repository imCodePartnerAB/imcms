package com.imcode.imcms.db.refactoring.model;

public class DdlUtilsReference implements Reference {

    private final org.apache.ddlutils.model.Reference reference;

    public DdlUtilsReference(org.apache.ddlutils.model.Reference reference) {
        this.reference = reference;
    }

    public String getLocalColumnName() {
        return reference.getLocalColumnName();
    }

    public String getForeignColumnName() {
        return reference.getForeignColumnName();
    }
}
