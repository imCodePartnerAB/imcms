package com.imcode.imcms.db;

import org.apache.commons.dbutils.ResultSetHandler;

import com.imcode.db.DatabaseException;

public interface ProcedureExecutor {

    int executeUpdateProcedure(String procedureName,
                               Object[] parameters) throws DatabaseException;

    Object executeProcedure(String procedureName, Object[] params,
                            ResultSetHandler resultSetHandler);
}