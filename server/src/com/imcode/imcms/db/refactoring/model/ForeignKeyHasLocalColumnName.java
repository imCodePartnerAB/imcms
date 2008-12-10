package com.imcode.imcms.db.refactoring.model;

import org.apache.commons.collections.Predicate;

public class ForeignKeyHasLocalColumnName implements Predicate {

    private final String columnName;

    public ForeignKeyHasLocalColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean evaluate(Object object) {
        ForeignKey foreignKey = (ForeignKey) object;
        for ( Reference reference : foreignKey.getReferences() ) {
            if (columnName.equals(reference.getLocalColumnName())) {
                return true;
            }
        }
        return false;
    }
}
