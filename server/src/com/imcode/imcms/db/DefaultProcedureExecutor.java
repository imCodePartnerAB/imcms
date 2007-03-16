package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlUpdateDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.util.CachingFileLoader;
import imcode.util.io.FileUtility;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultProcedureExecutor implements ProcedureExecutor {

    private final Database database;
    private final CachingFileLoader fileLoader;
    private Map procedureCache = new HashMap();
    private final static Logger log = Logger.getLogger( DefaultProcedureExecutor.class.getName() );

    public DefaultProcedureExecutor(Database database, CachingFileLoader fileLoader) {
        this.database = database;
        this.fileLoader = fileLoader;
    }

    public int executeUpdateProcedure(String procedureName,
                                      Object[] parameters) throws DatabaseException {
        Procedure procedure = getProcedure( procedureName );
        Object[] parametersAtCorrectIndices = getParametersAtCorrectIndicesForProcedure( procedure, parameters );
        String body = procedure.getBody();
        logProcedureCall(procedureName, body, parametersAtCorrectIndices);
        return ( (Integer) database.execute(new SqlUpdateDatabaseCommand(body, parametersAtCorrectIndices)) ).intValue() ;
    }

    private void logProcedureCall(String procedureName, String body, Object[] parametersAtCorrectIndices) {
        log.trace( "Calling procedure " + procedureName + " with parameters "
                   + ArrayUtils.toString( parametersAtCorrectIndices ) + " and body " + body );
    }

    public Object executeProcedure(String procedureName, Object[] params,
                                   ResultSetHandler resultSetHandler) {
        Procedure procedure = getProcedure( procedureName );
        Object[] parametersAtCorrectIndices = getParametersAtCorrectIndicesForProcedure( procedure, params );
        String body = procedure.getBody();
        logProcedureCall(procedureName, body, parametersAtCorrectIndices);
        return database.execute(new SqlQueryCommand(body, parametersAtCorrectIndices, resultSetHandler));
    }

    private Object[] getParametersAtCorrectIndicesForProcedure( Procedure procedure, Object[] parameters ) {
        int[] parameterIndices = procedure.getParameterIndices();
        Object[] parametersAtCorrectIndices = new String[parameterIndices.length];
        for ( int i = 0; i < parameterIndices.length; i++ ) {
            int parameterIndex = parameterIndices[i];
            parametersAtCorrectIndices[i] = parameters[parameterIndex];
        }
        return parametersAtCorrectIndices;
    }

    private Procedure getProcedure( String wantedProcedure ) {
        String procedureName = wantedProcedure.toLowerCase();
        File file = getFile( procedureName );
        Procedure procedure = (Procedure)procedureCache.get( procedureName );
        String procedureContents = fileLoader.getCachedFileStringIfRecent( file );
        if ( null == procedureContents ) {
            String procedureContents1 = loadFile(file);
            log.debug("Loading procedure "+procedureName) ;
            procedure = prepareProcedure(procedureContents1, procedureName );
            procedureCache.put( procedureName, procedure ) ;
        }
        return procedure;
    }

    Procedure prepareProcedure( String procedureContents, String procedureName ) {
        Pattern headerPattern = Pattern.compile( "CREATE\\s+PROCEDURE\\s+\\S+\\s+(.*)\\bAS\\s+", Pattern.CASE_INSENSITIVE
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
        String bodyWithParametersReplaced = replaceVariablesInBodyAndAddIndicesToList( parameterPattern, body, parameterNameToIndexMap, parameterIndices, procedureName );
        int[] parameterIndicesArray = ArrayUtils.toPrimitive( (Integer[])parameterIndices.toArray( new Integer[parameterIndices.size()] ) );
        return new Procedure(bodyWithParametersReplaced, parameterIndicesArray);
    }

    private String loadFile( File file ) {
        try {
            return fileLoader.getCachedFileString( file );
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
    }

    protected File getFile( String wantedProcedure ) {
        return FileUtility.getFileFromWebappRelativePath( "WEB-INF/sql/sprocs/"
                                                          + wantedProcedure.toLowerCase() + ".prc" );
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
                                                              Map parameterNameToIndexMap, List parameterIndices,
                                                              String procedureName ) {
        Matcher bodyParametersMatcher = parameterPattern.matcher( body );
        StringBuffer bodyStringBuffer = new StringBuffer();
        while ( bodyParametersMatcher.find() ) {
            bodyParametersMatcher.appendReplacement( bodyStringBuffer, "?" );
            String parameterName = bodyParametersMatcher.group( 1 );
            Integer parameterIndex = (Integer)parameterNameToIndexMap.get( parameterName );
            if (null == parameterIndex) {
                throw new IllegalArgumentException( "No parameter @"+parameterName+" in parameter list of procedure "+procedureName);
            }
            parameterIndices.add( parameterIndex );
        }
        bodyParametersMatcher.appendTail( bodyStringBuffer );
        return bodyStringBuffer.toString();
    }

    static class Procedure {

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