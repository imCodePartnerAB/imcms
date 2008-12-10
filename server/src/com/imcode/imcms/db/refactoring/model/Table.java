package com.imcode.imcms.db.refactoring.model;

import java.util.Collection;

public interface Table {

    String getName();

    Collection<Column> getColumns();
    
    Collection<ForeignKey> getForeignKeys();
}