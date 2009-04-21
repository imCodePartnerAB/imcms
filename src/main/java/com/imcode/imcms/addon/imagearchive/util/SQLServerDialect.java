package com.imcode.imcms.addon.imagearchive.util;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class SQLServerDialect extends org.hibernate.dialect.SQLServerDialect {
	public SQLServerDialect() {
		registerFunction("current_date", new CurrentDateSQLFunction());
	}
	
	private class CurrentDateSQLFunction implements SQLFunction {
		public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
			return Hibernate.DATE;
		}

		public boolean hasArguments() {
			return false;
		}

		public boolean hasParenthesesIfNoArguments() {
			return true;
		}

		@SuppressWarnings("unchecked")
        public String render(List args, SessionFactoryImplementor factory) throws QueryException {
			// truncate current datetime to date part, 
			// see this http://www.karaszi.com/SQLServer/info_datetime.asp
			
			return "DATEADD(DAY, DATEDIFF(DAY, '20080225', getdate()), '20080225')";
		}
	}
}
