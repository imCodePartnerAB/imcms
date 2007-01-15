package com.imcode.imcms.db.refactoring.model;

import java.util.List;

public class ForeignKeyWrapper extends AbstractForeignKey {

    private ForeignKey wrappedForeignKey;

    public ForeignKeyWrapper(ForeignKey wrappedForeignKey) {
        this.wrappedForeignKey = wrappedForeignKey;
    }

    public String getLocalTableName() {
        return wrappedForeignKey.getLocalTableName();
    }

    public String getName() {
        return wrappedForeignKey.getName();
    }

    public List<Reference> getReferences() {
        return wrappedForeignKey.getReferences();
    }

    public String getForeignTableName() {
        return wrappedForeignKey.getForeignTableName();
    }
}
