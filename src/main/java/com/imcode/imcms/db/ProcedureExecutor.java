package com.imcode.imcms.db;

import com.imcode.db.DatabaseException;
import org.apache.commons.dbutils.ResultSetHandler;

public interface ProcedureExecutor {

    int executeUpdateProcedure(String procedureName, Object[] parameters) throws DatabaseException;

    <T> T executeProcedure(String procedureName, Object[] params, ResultSetHandler<T> resultSetHandler);
}