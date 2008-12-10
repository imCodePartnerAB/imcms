package com.imcode.imcms.db.refactoring.model;

public interface Reference {

    String getLocalColumnName();

    String getForeignColumnName();
}