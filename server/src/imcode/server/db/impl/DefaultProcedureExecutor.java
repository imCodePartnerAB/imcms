package imcode.server.db.impl;

import imcode.server.db.ProcedureExecutor;
import imcode.util.FileUtility;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultProcedureExecutor implements ProcedureExecutor {

    private final static Logger log = Logger.getLogger( DefaultProcedureExecutor.class.getName() );
    private QueryRunner queryRunner;

    public DefaultProcedureExecutor( QueryRunner queryRunner ) {
        this.queryRunner = queryRunner;
    }

    private ProcedureSql replaceParametersInProcedure( String wantedProcedure,
                                                       String[] params ) {
        String procedureSql = loadProcedure( wantedProcedure );
        Pattern headerPattern = Pattern.compile( "CREATE\\s+PROCEDURE\\s+(\\S+)\\s+(.*)\\bAS\\b", Pattern.CASE_INSENSITIVE
                                                                                                  | Pattern.DOTALL );
        Matcher headerMatcher = headerPattern.matcher( procedureSql );
        if ( !headerMatcher.find() ) {
            throw new RuntimeException( "Failed to parse procedure " + wantedProcedure + ": " + procedureSql );
        }
        String procedureName = headerMatcher.group( 1 );
        String parametersSQL = headerMatcher.group( 2 );
        String body = procedureSql.substring( headerMatcher.end() );
        Pattern parameterPattern = Pattern.compile( "@(\\w+)" );
        Matcher parametersMatcher = parameterPattern.matcher( parametersSQL );
        Map parameterMap = new HashMap();
        int headerParametersFound = 0;
        while ( parametersMatcher.find() ) {
            String parameterName = parametersMatcher.group( 1 );
            parameterMap.put( parameterName, params[headerParametersFound++] );
        }
        List parameters = new ArrayList();
        Matcher bodyParametersMatcher = parameterPattern.matcher( body );
        StringBuffer bodyStringBuffer = new StringBuffer();
        while ( bodyParametersMatcher.find() ) {
            bodyParametersMatcher.appendReplacement( bodyStringBuffer, "?" );
            String parameterName = bodyParametersMatcher.group( 1 );
            String parameterValue = (String)parameterMap.get( parameterName );
            parameters.add( parameterValue );
        }
        bodyParametersMatcher.appendTail( bodyStringBuffer );
        String bodyWithParametersReplaced = bodyStringBuffer.toString();
        String[] replacedParametersArray = (String[])parameters.toArray( new String[parameters.size()] );
        log.debug( "Procedure " + procedureName + " called with parameters\n"
                   + ArrayUtils.toString( replacedParametersArray ) + " and body\n" + bodyWithParametersReplaced );
        return new ProcedureSql( bodyWithParametersReplaced, replacedParametersArray );
    }

    private String loadProcedure( String wantedProcedure ) {
        String procedureSql;
        try {
            File procedureFile = FileUtility.getFileFromWebappRelativePath( "WEB-INF/sql/sprocs/"
                                                                            + wantedProcedure.toLowerCase() + ".prc" );
            procedureSql = IOUtils.toString( new FileInputStream( procedureFile ) );
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
        return procedureSql;
    }

    public int executeUpdateProcedure( Connection connection, String procedure, String[] params ) throws SQLException {
        ProcedureSql procedureSql = replaceParametersInProcedure( procedure, params );
        return queryRunner.update( connection, procedureSql.getSql(), procedureSql.getParameters() );
    }

    public Object executeProcedure( Connection connection, String procedure, String[] params,
                             ResultSetHandler resultSetHandler ) throws SQLException {
        ProcedureSql procedureSql = replaceParametersInProcedure( procedure, params );
        return queryRunner.query( connection, procedureSql.getSql(), procedureSql.getParameters(), resultSetHandler );
    }

    private static class ProcedureSql {

        private String sql;
        private String[] parameters;

        ProcedureSql( String sql, String[] parameters ) {
            this.sql = sql;
            this.parameters = parameters;
        }

        public String[] getParameters() {
            return parameters;
        }

        public String getSql() {
            return sql;
        }
    }

}
