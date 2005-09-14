package imcode.server.db.impl;

import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.exceptions.DatabaseException;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockDatabase implements Database {

    private List sqlCalls = new ArrayList();
    private List expectedSqlCalls = new ArrayList();

    public int executeUpdateProcedure(String procedure, Object[] parameters) {
        getResultForSqlCall(procedure, parameters);
        return 0;
    }

    public int executeUpdateQuery(String sqlStr, Object[] parameters) {
        getResultForSqlCall(sqlStr, parameters);
        return 0;
    }


    public Object executeQuery(String sqlQuery, Object[] parameters, ResultSetHandler resultSetHandler) {
        ResultSet resultSet = (ResultSet) getResultForSqlCall(sqlQuery, parameters);
        if (null == resultSet ) {
            resultSet = new MockResultSet(new Object[0][]) ;
        }
        try {
            return resultSetHandler.handle(resultSet) ;
        } catch ( SQLException e ) {
            throw DatabaseException.fromSQLException("", e);
        }
    }

    public Object executeCommand(DatabaseCommand databaseCommand) throws DatabaseException {
        return databaseCommand.executeOn(new MockDatabaseConnection(this));
    }

    public void addExpectedSqlCall(final SqlCallPredicate sqlCallPredicate, final Object result) {
        expectedSqlCalls.add(new Map.Entry() {
            public Object getKey() {
                return sqlCallPredicate;
            }

            public Object getValue() {
                return result;
            }

            public Object setValue(Object value) {
                throw new UnsupportedOperationException();
            }

            public String toString() {
                return sqlCallPredicate + ": " + result;
            }
        });
    }

    public void assertExpectedSqlCalls() {
        if (!expectedSqlCalls.isEmpty()) {
            Assert.fail("Remaining expected sql calls: " + expectedSqlCalls.toString());
        }
    }

    public int getSqlCallCount() {
        return sqlCalls.size();
    }

    Object getResultForSqlCall(String sql, Object[] params) {
        SqlCall sqlCall = new SqlCall(sql, params);
        sqlCalls.add(sqlCall);
        Object result = null;
        if (!expectedSqlCalls.isEmpty()) {
            Map.Entry entry = (Map.Entry) expectedSqlCalls.get(0);
            SqlCallPredicate predicate = (SqlCallPredicate) entry.getKey();
            if (predicate.evaluateSqlCall(sqlCall)) {
                result = entry.getValue();
                expectedSqlCalls.remove(0);
            }
        }
        return result;
    }

    public static class SqlCall {

        private String string;
        private Object[] parameters;

        public SqlCall(String string, Object[] parameters) {
            this.string = string;
            this.parameters = parameters;
        }

        public String getString() {
            return string;
        }

        public Object[] getParameters() {
            return parameters;
        }

        public String toString() {
            return getString() + " " + StringUtils.join(getParameters(), ", ");
        }

    }

    public void assertCalled(SqlCallPredicate predicate) {
        assertCalled(null, predicate);
    }

    public void assertCalledInOrder(SqlCallPredicate[] sqlCallPredicates) {
        int sqlCallPredicatesIndex = 0 ;
        for ( Iterator iterator = sqlCalls.iterator(); iterator.hasNext(); ) {
            SqlCall sqlCall = (SqlCall) iterator.next();
            if (sqlCallPredicates[sqlCallPredicatesIndex].evaluateSqlCall(sqlCall)) {
                sqlCallPredicatesIndex++ ;
                if (sqlCallPredicatesIndex == sqlCallPredicates.length) {
                    break ;
                }
            }
        }
        if (sqlCallPredicatesIndex < sqlCallPredicates.length) {
            String failureMessage = "Expected sql call \"" + sqlCallPredicates[sqlCallPredicatesIndex].getFailureMessage()+"\"";
            if (sqlCallPredicatesIndex > 0) {
                failureMessage += " after sql call \""+sqlCallPredicates[sqlCallPredicatesIndex-1]+"\"" ;
            }
            Assert.fail(failureMessage) ;
        }
    }

    public void assertCalled(String message, SqlCallPredicate predicate) {
        if (!called(predicate)) {
            String messagePrefix = null == message ? "" : message + " ";
            Assert.fail(messagePrefix + "Expected at least one sql call: " + predicate.getFailureMessage());
        }
    }

    private boolean called(SqlCallPredicate predicate) {
        return CollectionUtils.exists(sqlCalls, predicate);
    }

    public void assertNotCalled(SqlCallPredicate sqlCallPredicate) {
        assertNotCalled(null, sqlCallPredicate);
    }

    public void assertNotCalled(String message, SqlCallPredicate predicate) {
        if (called(predicate)) {
            String messagePrefix = null == message ? "" : message + " ";
            Assert.fail(messagePrefix + "Got unexpected sql call: " + predicate.getFailureMessage());
        }
    }

    public void assertCallCount(int expectedCount, SqlCallPredicate predicate) {
        int actualCount = CollectionUtils.countMatches(sqlCalls, predicate);
        if (expectedCount != actualCount) {
            Assert.fail("Expected " + expectedCount + ", but got " + actualCount + " sql calls: " + predicate.getFailureMessage());
        }
    }

    public abstract static class SqlCallPredicate implements Predicate {

        public final boolean evaluate(Object object) {
            return evaluateSqlCall((MockDatabase.SqlCall) object);
        }

        abstract boolean evaluateSqlCall(MockDatabase.SqlCall sqlCall);

        abstract String getFailureMessage();

        public String toString() {
            return getFailureMessage();
        }
    }

    public static class UpdateTableSqlCallPredicate extends SqlCallPredicate {

        private String tableName;
        private Object parameter;

        public UpdateTableSqlCallPredicate(String tableName, Object parameter) {
            this.tableName = tableName;
            this.parameter = parameter;
        }

        boolean evaluateSqlCall(MockDatabase.SqlCall sqlCall) {
            boolean stringMatchesUpdateTableName = Pattern.compile("^update\\s+\\b" + tableName+"\\b").matcher(sqlCall.getString().toLowerCase()).find();
            boolean parametersContainsParameter = ArrayUtils.contains(sqlCall.getParameters(), parameter);
            return stringMatchesUpdateTableName && parametersContainsParameter;
        }

        String getFailureMessage() {
            return "update of table " + tableName + " with one parameter = " + parameter;
        }
    }

    public static class InsertIntoTableSqlCallPredicate extends SqlCallPredicate {

        private String tableName;

        public InsertIntoTableSqlCallPredicate(String tableName) {
            this.tableName = tableName;
        }

        boolean evaluateSqlCall(SqlCall sqlCall) {
            Pattern pattern = Pattern.compile("^insert\\s+(?:into\\s+)?" + tableName, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sqlCall.getString());
            return matcher.find();
        }

        String getFailureMessage() {
            return "insert into table " + tableName ;
        }
    }

    public static class InsertIntoTableWithParameterSqlCallPredicate extends InsertIntoTableSqlCallPredicate {

        private String parameter;

        public InsertIntoTableWithParameterSqlCallPredicate(String tableName, String parameter) {
            super(tableName);
            this.parameter = parameter;
        }

        boolean evaluateSqlCall(MockDatabase.SqlCall sqlCall) {
            return super.evaluateSqlCall(sqlCall) && ArrayUtils.contains(sqlCall.getParameters(), parameter);
        }

        String getFailureMessage() {
            return super.getFailureMessage() + " with one parameter = \"" + parameter + "\"";
        }
    }

    public static class MatchesRegexSqlCallPredicate extends SqlCallPredicate {

        private String regex;

        public MatchesRegexSqlCallPredicate(String regex) {
            this.regex = regex;
        }

        boolean evaluateSqlCall(MockDatabase.SqlCall sqlCall) {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sqlCall.getString());
            return matcher.find();
        }

        String getFailureMessage() {
            return "Expected call to match regex " + regex;
        }
    }

    public static class EqualsSqlCallPredicate extends SqlCallPredicate {

        String sql;

        public EqualsSqlCallPredicate(String sql) {
            this.sql = sql;
        }

        boolean evaluateSqlCall(SqlCall sqlCall) {
            return sql.equalsIgnoreCase(sqlCall.getString());
        }

        String getFailureMessage() {
            return "sql \"" + sql + "\"";
        }
    }

    public static class StartsWithSqlCallPredicate extends SqlCallPredicate {

        private String prefix;

        public StartsWithSqlCallPredicate(String prefix) {
            this.prefix = prefix;
        }

        boolean evaluateSqlCall(SqlCall sqlCall) {
            return sqlCall.getString().startsWith(prefix);
        }

        String getFailureMessage() {
            return "start with " + prefix;
        }
    }

    public static class EqualsWithParametersSqlCallPredicate extends EqualsSqlCallPredicate {

        private String[] parameters;

        public EqualsWithParametersSqlCallPredicate(String sql, String[] parameters) {
            super(sql);
            this.parameters = parameters;
        }

        boolean evaluateSqlCall(SqlCall sqlCall) {
            return super.evaluateSqlCall(sqlCall) && Arrays.equals(parameters, sqlCall.getParameters());
        }

        String getFailureMessage() {
            return super.getFailureMessage() + " with parameters " + ArrayUtils.toString(parameters);
        }
    }

    public static class DeleteFromTableSqlCallPredicate extends SqlCallPredicate {

        private String tableName;

        public DeleteFromTableSqlCallPredicate(String tableName) {
            this.tableName = tableName;
        }

        boolean evaluateSqlCall(SqlCall sqlCall) {
            Pattern pattern = Pattern.compile("^delete\\s+from\\s+\\b" + tableName+"\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sqlCall.getString());
            return matcher.find();
        }

        String getFailureMessage() {
            return "delete from "+tableName;
        }

    }
}
