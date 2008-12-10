package com.imcode.imcms.db.refactoring.model;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractForeignKey implements ForeignKey {

    public List<String> getLocalColumnNames() {
        List<String> localColumnNames = new ArrayList<String>();
        for ( Reference reference : getReferences() ) {
            localColumnNames.add(reference.getLocalColumnName());
        }
        return localColumnNames;
    }

    public List<String> getForeignColumnNames() {
        List<String> foreignColumnNames = new ArrayList<String>();
        for ( Reference reference : getReferences() ) {
            foreignColumnNames.add(reference.getForeignColumnName());
        }
        return foreignColumnNames;
    }
}
