package imcode.server.db;

import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MockDatabase implements Database {

    private List sqlCalls = new ArrayList();
    private List expectedSqlCalls = new ArrayList();

    public String[] sqlProcedure( String procedure, String[] params ) {
        String[] result = (String[])getResultForSqlCall( procedure, params );
        if (null == result) {
            return new String[0] ;
        }
        return result;
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        return (Map)getResultForSqlCall( procedure, params );
    }

    public int sqlUpdateProcedure( String procedure, String[] params ) {
        getResultForSqlCall( procedure, params );
        return 0;
    }

    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        getResultForSqlCall( sqlStr, params );
        return 0;
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return (String)getResultForSqlCall( procedure, params );
    }

    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        String[][] result = (String[][])getResultForSqlCall( procedure, params );
        if (null == result) {
            result = new String[0][0];
        }
        return result;
    }

    public String[] sqlQuery( String sqlStr, String[] params ) {
        String[] result = (String[])getResultForSqlCall( sqlStr, params );
        if (null == result) {
            result = new String[0];
        }
        return result;
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return (String)getResultForSqlCall( sqlStr, params );
    }

    public String[][] sqlQueryMulti( String sqlstr, String[] params ) {
        String[][] result = (String[][])getResultForSqlCall( sqlstr, params );
        if (null == result) {
            result = new String[0][0];
        }
        return result;
    }

    public void executeTransaction( DatabaseCommand databaseCommand ) {
        // TODO
    }

    public void addExpectedSqlCall( final SqlCallPredicate sqlCallPredicate, final Object result ) {
        expectedSqlCalls.add( new Map.Entry() {
            public Object getKey() {
                return sqlCallPredicate;
            }

            public Object getValue() {
                return result;
            }

            public Object setValue( Object value ) {
                throw new UnsupportedOperationException();
            }

            public String toString() {
                return sqlCallPredicate+": "+result ;
            }
        } );
    }

    public void verifyExpectedSqlCalls() {
        if ( !expectedSqlCalls.isEmpty() ) {
            Assert.fail( "Remaining expected sql calls: " + expectedSqlCalls.toString() );
        }
    }

    public int getSqlCallCount() {
        return sqlCalls.size();
    }

    private Object getResultForSqlCall( String procedure, String[] params ) {
        SqlCall sqlCall = new SqlCall( procedure, params );
        sqlCalls.add( sqlCall );
        Object result = null;
        if ( !expectedSqlCalls.isEmpty() ) {
            Map.Entry entry = (Map.Entry)expectedSqlCalls.get( 0 );
            SqlCallPredicate predicate = (SqlCallPredicate)entry.getKey();
            if ( predicate.evaluateSqlCall( sqlCall ) ) {
                result = entry.getValue();
                expectedSqlCalls.remove( 0 ) ;
            }
        }
        return result;
    }

    public List getSqlCalls() {
        return sqlCalls;
    }

    public static class SqlCall {

        private String string;
        private String[] parameters;
        private Object result;

        public SqlCall( String string, String[] parameters ) {
            this.string = string;
            this.parameters = parameters;
        }

        public SqlCall( String string, String[] parameters, Object result ) {
            this( string, parameters );
            this.result = result;
        }

        public String getString() {
            return string;
        }

        public String[] getParameters() {
            return parameters;
        }

        public Object getResult() {
            return result;
        }

        public String toString() {
            return getString()+" "+ StringUtils.join( getParameters(), ", " );
        }

        public void setParameters( String[] parameters ) {
            this.parameters = parameters;
        }
    }

    public void assertCalled( SqlCallPredicate predicate ) {
        assertCalled( null, predicate );
    }

    public void assertCalled( String message, SqlCallPredicate predicate ) {
        if ( !called( predicate ) ) {
            String messagePrefix = null == message ? "" : message + " ";
            Assert.fail( messagePrefix + "Expected at least one sql call: " + predicate.getFailureMessage() );
        }
    }

    private boolean called( SqlCallPredicate predicate ) {
        return CollectionUtils.exists( sqlCalls, predicate );
    }

    public void assertNotCalled( SqlCallPredicate sqlCallPredicate ) {
        assertNotCalled( null, sqlCallPredicate ) ;
    }

    public void assertNotCalled( String message, SqlCallPredicate predicate ) {
        if ( called( predicate ) ) {
            String messagePrefix = null == message ? "" : message + " ";
            Assert.fail( messagePrefix + "Got unexpected sql call: " + predicate.getFailureMessage() );
        }
    }

    public void assertCallCount( int expectedCount, SqlCallPredicate predicate ) {
        int actualCount = CollectionUtils.countMatches( sqlCalls, predicate );
        if ( expectedCount != actualCount ) {
            Assert.fail( "Expected " + expectedCount + ", but got "+actualCount+" sql calls: " + predicate.getFailureMessage() );
        }
    }

    public abstract static class SqlCallPredicate implements Predicate {

        public boolean evaluate( Object object ) {
            return evaluateSqlCall( (MockDatabase.SqlCall)object );
        }

        abstract boolean evaluateSqlCall( MockDatabase.SqlCall sqlCall );

        abstract String getFailureMessage();

        public String toString() {
            return getFailureMessage() ;
        }
    }

    public static class ProcedureSqlCallPredicate extends SqlCallPredicate {

        String procedureName;

        public ProcedureSqlCallPredicate( String procedureName ) {
            this.procedureName = procedureName;
        }

        boolean evaluateSqlCall( SqlCall sqlCall ) {
            return procedureName.equalsIgnoreCase( sqlCall.getString() );
        }

        String getFailureMessage() {
            return "procedure with name " + procedureName;
        }
    }

    public static class UpdateTableSqlCallPredicate extends SqlCallPredicate {

        private String tableName;
        private String parameter;

        public UpdateTableSqlCallPredicate( String tableName, String parameter ) {
            this.tableName = tableName;
            this.parameter = parameter;
        }

        boolean evaluateSqlCall( MockDatabase.SqlCall sqlCall ) {
            boolean stringMatchesUpdateTableName = Pattern.compile( "^update\\s+" + tableName ).matcher( sqlCall.getString().toLowerCase() ).find();
            boolean parametersContainsParameter = ArrayUtils.contains( sqlCall.getParameters(), parameter );
            return stringMatchesUpdateTableName && parametersContainsParameter;
        }

        String getFailureMessage() {
            return "update of table " + tableName + " with one parameter = " + parameter;
        }
    }

    public static class InsertIntoTableSqlCallPredicate extends SqlCallPredicate {

        private String tableName;

        public InsertIntoTableSqlCallPredicate( String tableName ) {
            this.tableName = tableName;
        }

        boolean evaluateSqlCall( SqlCall sqlCall ) {
            Pattern pattern = Pattern.compile( "^insert\\s+(?:into\\s+)?" + tableName, Pattern.CASE_INSENSITIVE );
            Matcher matcher = pattern.matcher( sqlCall.getString() );
            boolean result = matcher.find();
            return result;
        }

        String getFailureMessage() {
            return "insert into table \""+tableName+"\"" ;
        }
    }

    public static class InsertIntoTableWithParameterSqlCallPredicate extends InsertIntoTableSqlCallPredicate {

        private String parameter;

        public InsertIntoTableWithParameterSqlCallPredicate( String tableName, String parameter ) {
            super(tableName);
            this.parameter = parameter;
        }

        boolean evaluateSqlCall( MockDatabase.SqlCall sqlCall ) {
            return super.evaluateSqlCall( sqlCall ) && ArrayUtils.contains( sqlCall.getParameters(), parameter );
        }

        String getFailureMessage() {
            return super.getFailureMessage() + " with one parameter = \"" + parameter + "\"";
        }
    }

    public static class MatchesRegexSqlCallPredicate extends SqlCallPredicate {

        private String regex;

        public MatchesRegexSqlCallPredicate( String regex ) {
            this.regex = regex;
        }

        boolean evaluateSqlCall( MockDatabase.SqlCall sqlCall ) {
            Pattern pattern = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
            Matcher matcher = pattern.matcher( sqlCall.getString() );
            boolean result = matcher.find();
            return result;
        }

        String getFailureMessage() {
            return "Expected call to match regex " + regex;
        }
    }

    public static class EqualsSqlCallPredicate extends SqlCallPredicate {

        String sql;

        public EqualsSqlCallPredicate( String sql ) {
            this.sql = sql;
        }

        boolean evaluateSqlCall( SqlCall sqlCall ) {
            return sql.equalsIgnoreCase( sqlCall.getString() );
        }

        String getFailureMessage() {
            return "sql \"" + sql+"\"";
        }
    }

    public static class EqualsWithParameterSqlCallPredicate extends EqualsSqlCallPredicate {

        private Object parameterValue;

        public EqualsWithParameterSqlCallPredicate( String sql, Object parameterValue ) {
            super( sql );
            this.parameterValue = parameterValue;
        }

        boolean evaluateSqlCall( SqlCall sqlCall ) {
            return super.evaluateSqlCall( sqlCall ) && ArrayUtils.contains( sqlCall.getParameters(), parameterValue ) ;
        }

        String getFailureMessage() {
            return "parameter value \"" + parameterValue + "\" for sql " + sql;
        }
    }

    public static class StartsWithSqlCallPredicate extends SqlCallPredicate {

        private String prefix;

        public StartsWithSqlCallPredicate( String prefix ) {
            this.prefix = prefix;
        }

        boolean evaluateSqlCall( SqlCall sqlCall ) {
            return sqlCall.getString().startsWith( prefix );
        }

        String getFailureMessage() {
            return "start with " + prefix;
        }
    }

    public static class EqualsWithParametersSqlCallPredicate extends EqualsSqlCallPredicate {

        private String[] parameters;

        public EqualsWithParametersSqlCallPredicate( String sql, String[] params ) {
            super(sql);
            this.parameters = params ;
        }

        boolean evaluateSqlCall( SqlCall sqlCall ) {
            return super.evaluateSqlCall( sqlCall ) && Arrays.equals(parameters,sqlCall.getParameters()) ;
        }

        String getFailureMessage() {
            return super.getFailureMessage() + " with parameters "+ArrayUtils.toString( parameters );
        }
    }
}
