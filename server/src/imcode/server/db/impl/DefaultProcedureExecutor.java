package imcode.server.db.impl;

import imcode.server.db.ProcedureExecutor;
import imcode.util.FileCache;
import imcode.util.FileUtility;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultProcedureExecutor implements ProcedureExecutor {

    private QueryRunner queryRunner;
    private FileCache fileCache = new FileCache();
    private Map procedureCache = new HashMap();
    private final static Logger log = Logger.getLogger( DefaultProcedureExecutor.class.getName() );

    public DefaultProcedureExecutor( QueryRunner queryRunner ) {
        this.queryRunner = queryRunner;
    }

    public int executeUpdateProcedure( Connection connection, String procedureName,
                                       String[] params ) throws SQLException {
        Procedure procedure = getProcedure( procedureName );
        Object[] parameters = getParametersForProcedure( procedure, params );
        String body = procedure.getBody();
        log.debug( "Calling procedure " + procedureName + " with body " + body + " and parameters "
                   + ArrayUtils.toString( parameters ) );
        return queryRunner.update( connection, body, parameters );
    }

    public Object executeProcedure( Connection connection, String procedureName, String[] params,
                                    ResultSetHandler resultSetHandler ) throws SQLException {
        Procedure procedure = getProcedure( procedureName );
        Object[] parameters = getParametersForProcedure( procedure, params );
        String body = procedure.getBody();
        log.debug( "Calling procedure " + procedureName + " with body " + body + " and parameters "
                   + ArrayUtils.toString( parameters ) );
        return queryRunner.query( connection, body, parameters, resultSetHandler );
    }

    private Object[] getParametersForProcedure( Procedure procedure, String[] params ) {
        int[] parameterIndices = procedure.getParameterIndices();
        Object[] parameters = new String[parameterIndices.length];
        for ( int i = 0; i < parameterIndices.length; i++ ) {
            int parameterIndex = parameterIndices[i];
            parameters[i] = params[parameterIndex];
        }
        return parameters;
    }

    private Procedure getProcedure( String wantedProcedure ) {
        String procedureName = wantedProcedure.toLowerCase();
        File file = getFile( procedureName );
        Procedure procedure = (Procedure)procedureCache.get( procedureName );
        String procedureContents = fileCache.getCachedFileStringIfRecent( file );
        if ( null == procedureContents ) {
            procedureContents = loadFile( file );
            log.debug("Loading procedure "+procedureName) ;
            procedure = prepareProcedure( procedureContents, procedureName );
            procedureCache.put( procedureName, procedure ) ;
        }
        return procedure;
    }

    private Procedure prepareProcedure( String procedureContents, String procedureName ) {
        Procedure procedure;
        Pattern headerPattern = Pattern.compile( "CREATE\\s+PROCEDURE\\s+\\S+\\s+(.*)\\bAS\\b", Pattern.CASE_INSENSITIVE
                                                                                                  | Pattern.DOTALL );
        Matcher headerMatcher = headerPattern.matcher( procedureContents );
        if ( !headerMatcher.find() ) {
            throw new RuntimeException( "Failed to parse procedure " + procedureName + ": " + procedureContents );
        }
        String headerParameters = headerMatcher.group( 1 );
        String body = procedureContents.substring( headerMatcher.end() );

        Pattern parameterPattern = Pattern.compile( "@(\\w+)" );
        Map parameterNameToIndexMap = getParameterNameToIndexMapParsedFromHeader( parameterPattern, headerParameters );
        List parameterIndices = new ArrayList();
        String bodyWithParametersReplaced = replaceVariablesInBodyAndAddIndicesToList( parameterPattern, body, parameterNameToIndexMap, parameterIndices );
        int[] parameterIndicesArray = ArrayUtils.toPrimitive( (Integer[])parameterIndices.toArray( new Integer[parameterIndices.size()] ) );
        procedure = new Procedure( bodyWithParametersReplaced, parameterIndicesArray );
        return procedure;
    }

    private String loadFile( File file ) {
        try {
            return fileCache.getCachedFileString( file );
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
    }

    private File getFile( String wantedProcedure ) {
        File procedureFile = FileUtility.getFileFromWebappRelativePath( "WEB-INF/sql/sprocs/"
                                                                        + wantedProcedure.toLowerCase() + ".prc" );
        return procedureFile;
    }

    private Map getParameterNameToIndexMapParsedFromHeader( Pattern parameterPattern, String headerParameters ) {
        Matcher parametersMatcher = parameterPattern.matcher( headerParameters );
        Map parameterNameToIndexMap = new HashMap();
        int headerParametersFound = 0;
        while ( parametersMatcher.find() ) {
            String parameterName = parametersMatcher.group( 1 );
            parameterNameToIndexMap.put( parameterName, new Integer( headerParametersFound++ ) );
        }
        return parameterNameToIndexMap;
    }

    private String replaceVariablesInBodyAndAddIndicesToList( Pattern parameterPattern, String body,
                                                              Map parameterNameToIndexMap, List parameterIndices ) {
        Matcher bodyParametersMatcher = parameterPattern.matcher( body );
        StringBuffer bodyStringBuffer = new StringBuffer();
        while ( bodyParametersMatcher.find() ) {
            bodyParametersMatcher.appendReplacement( bodyStringBuffer, "?" );
            String parameterName = bodyParametersMatcher.group( 1 );
            Integer parameterIndex = (Integer)parameterNameToIndexMap.get( parameterName );
            parameterIndices.add( parameterIndex );
        }
        bodyParametersMatcher.appendTail( bodyStringBuffer );
        return bodyStringBuffer.toString();
    }

    private static class Procedure {

        private String sql;
        private int[] parameterIndices;

        Procedure( String sql, int[] parameterIndices ) {
            this.sql = sql;
            this.parameterIndices = parameterIndices;
        }

        public int[] getParameterIndices() {
            return parameterIndices;
        }

        public String getBody() {
            return sql;
        }
    }

}
