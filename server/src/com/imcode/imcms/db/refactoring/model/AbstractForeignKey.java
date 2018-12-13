package com.imcode.imcms.db.refactoring.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractForeignKey implements ForeignKey {

    public List<String> getLocalColumnNames() {
        List<String> localColumnNames = new ArrayList<>();
        for (Reference reference : getReferences()) {
            localColumnNames.add(reference.getLocalColumnName());
        }
        return localColumnNames;
    }

    public List<String> getForeignColumnNames() {
        List<String> foreignColumnNames = new ArrayList<>();
        for (Reference reference : getReferences()) {
            foreignColumnNames.add(reference.getForeignColumnName());
        }
        return foreignColumnNames;
    }
}
