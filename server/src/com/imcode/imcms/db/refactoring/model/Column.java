package com.imcode.imcms.db.refactoring.model;

public interface Column {

    boolean isNullable();

    boolean hasDefault();

    String getDefaultValue();

    boolean isAutoIncremented();

    boolean isPrimaryKey();

    Type getType();

    int getSize();

    String getName();
}
