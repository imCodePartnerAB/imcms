package com.imcode.imcms.addon.imagearchive.util;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;
// todo: check in use
public class SQLServerDialect extends org.hibernate.dialect.SQLServerDialect {
    public SQLServerDialect() {
        registerFunction("current_date", new CurrentDateSQLFunction());
    }

    private class CurrentDateSQLFunction implements SQLFunction {
        public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
            return org.hibernate.type.StandardBasicTypes.DATE;
        }

        public boolean hasArguments() {
            return false;
        }

        public boolean hasParenthesesIfNoArguments() {
            return true;
        }

        @SuppressWarnings("unchecked")
        public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
            // truncate current datetime to date part,
            // see this http://www.karaszi.com/SQLServer/info_datetime.asp

            return "DATEADD(DAY, DATEDIFF(DAY, '20080225', getdate()), '20080225')";
        }
    }
}
