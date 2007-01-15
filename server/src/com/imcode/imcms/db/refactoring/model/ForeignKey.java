package com.imcode.imcms.db.refactoring.model;

import java.util.List;

public interface ForeignKey {

    String getLocalTableName();
    String getName();

    List<Reference> getReferences();

    List<String> getLocalColumnNames();

    List<String> getForeignColumnNames();

    String getForeignTableName();
}