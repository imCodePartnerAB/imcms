package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.persistence.components.SqlResourcePathResolver;
import imcode.util.CachingFileLoader;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DefaultProcedureExecutor implements ProcedureExecutor {

    private final static Logger log = Logger.getLogger(DefaultProcedureExecutor.class.getName());
    private final Database database;
    private final CachingFileLoader fileLoader;
    private final Map<String, Procedure> procedureCache = CachingFileLoader.createSynchronizedMapDefaultInitialSize();
    private final Resource sqlResource;

    public DefaultProcedureExecutor(Database database,
                                    CachingFileLoader fileLoader,
                                    @Value("classpath:sql") Resource sqlResource) {

        this.database = database;
        this.fileLoader = fileLoader;
        this.sqlResource = sqlResource;
    }

    @Override
    public int executeUpdateProcedure(String procedureName, Object[] parameters) throws DatabaseException {
        Procedure procedure = getProcedure(procedureName);
        Object[] parametersAtCorrectIndices = getParametersAtCorrectIndicesForProcedure(procedure, parameters);
        String body = procedure.getBody();
        logProcedureCall(procedureName, body, parametersAtCorrectIndices);
        return (Integer) database.execute(new SqlUpdateCommand(body, parametersAtCorrectIndices));
    }

    private void logProcedureCall(String procedureName, String body, Object[] parametersAtCorrectIndices) {
        log.trace("Calling procedure " + procedureName + " with parameters "
                + ArrayUtils.toString(parametersAtCorrectIndices) + " and body " + body);
    }

    @Override
    public <T> T executeProcedure(String procedureName, Object[] params, ResultSetHandler resultSetHandler) {
        Procedure procedure = getProcedure(procedureName);
        Object[] parametersAtCorrectIndices = getParametersAtCorrectIndicesForProcedure(procedure, params);
        String body = procedure.getBody();
        logProcedureCall(procedureName, body, parametersAtCorrectIndices);
        return (T) database.execute(new SqlQueryCommand(body, parametersAtCorrectIndices, resultSetHandler));
    }

    private Object[] getParametersAtCorrectIndicesForProcedure(Procedure procedure, Object[] parameters) {
        int[] parameterIndices = procedure.getParameterIndices();
        Object[] parametersAtCorrectIndices = new String[parameterIndices.length];
        for (int i = 0; i < parameterIndices.length; i++) {
            int parameterIndex = parameterIndices[i];
            parametersAtCorrectIndices[i] = parameters[parameterIndex];
        }
        return parametersAtCorrectIndices;
    }

    private Procedure getProcedure(String wantedProcedure) {
        final String procedureName = wantedProcedure.toLowerCase();
        Procedure procedure = procedureCache.get(procedureName);

        if (procedure == null) {
            log.debug("Loading procedure " + procedureName);

            try (SqlResourcePathResolver pathResolver = new SqlResourcePathResolver(sqlResource.getURI())) {
                final String procedureSubPath = "sprocs/" + wantedProcedure.toLowerCase() + ".prc";
                final Path procedurePath = pathResolver.resolveSqlResourceSubPath(procedureSubPath);
                final String procedureContents = loadFile(procedurePath);

                procedure = prepareProcedure(procedureContents, procedureName);
                procedureCache.put(procedureName, procedure);

            } catch (Exception e) {
                final String message = "SQL procedure resource path can't be resolved!";
                log.error(message, e);
                e.printStackTrace();
                throw new RuntimeException(message);
            }
        }

        return procedure;
    }

    private Procedure prepareProcedure(String procedureContents, String procedureName) {
        Pattern headerPattern = Pattern.compile("CREATE\\s+PROCEDURE\\s+\\S+\\s+(.*)\\bAS\\s+", Pattern.CASE_INSENSITIVE
                | Pattern.DOTALL);
        Matcher headerMatcher = headerPattern.matcher(procedureContents);
        if (!headerMatcher.find()) {
            throw new RuntimeException("Failed to parse procedure " + procedureName + ": " + procedureContents);
        }
        String headerParameters = headerMatcher.group(1);
        String body = procedureContents.substring(headerMatcher.end());

        Pattern parameterPattern = Pattern.compile("@(\\w+)");
        Map<String, Integer> parameterNameToIndexMap = getParameterNameToIndexMapParsedFromHeader(parameterPattern, headerParameters);
        List<Integer> parameterIndices = new ArrayList<>();
        String bodyWithParametersReplaced = replaceVariablesInBodyAndAddIndicesToList(parameterPattern, body, parameterNameToIndexMap, parameterIndices, procedureName);
        int[] parameterIndicesArray = ArrayUtils.toPrimitive(parameterIndices.toArray(new Integer[parameterIndices.size()]));
        return new Procedure(bodyWithParametersReplaced, parameterIndicesArray);
    }

    private String loadFile(Path filePath) {
        try {
            return fileLoader.getCachedFileString(filePath);
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    private Map<String, Integer> getParameterNameToIndexMapParsedFromHeader(Pattern parameterPattern, String headerParameters) {
        Matcher parametersMatcher = parameterPattern.matcher(headerParameters);
        Map<String, Integer> parameterNameToIndexMap = new HashMap<>();
        int headerParametersFound = 0;
        while (parametersMatcher.find()) {
            String parameterName = parametersMatcher.group(1);
            parameterNameToIndexMap.put(parameterName, headerParametersFound++);
        }
        return parameterNameToIndexMap;
    }

    private String replaceVariablesInBodyAndAddIndicesToList(Pattern parameterPattern, String body,
                                                             Map<String, Integer> parameterNameToIndexMap, List<Integer> parameterIndices,
                                                             String procedureName) {
        Matcher bodyParametersMatcher = parameterPattern.matcher(body);
        StringBuffer bodyStringBuffer = new StringBuffer();
        while (bodyParametersMatcher.find()) {
            bodyParametersMatcher.appendReplacement(bodyStringBuffer, "?");
            String parameterName = bodyParametersMatcher.group(1);
            Integer parameterIndex = parameterNameToIndexMap.get(parameterName);
            if (null == parameterIndex) {
                throw new IllegalArgumentException("No parameter @" + parameterName + " in parameter list of procedure " + procedureName);
            }
            parameterIndices.add(parameterIndex);
        }
        bodyParametersMatcher.appendTail(bodyStringBuffer);
        return bodyStringBuffer.toString();
    }

    static class Procedure {

        private String sql;
        private int[] parameterIndices;

        Procedure(String sql, int[] parameterIndices) {
            this.sql = sql;
            this.parameterIndices = parameterIndices;
        }

        int[] getParameterIndices() {
            return parameterIndices;
        }

        public String getBody() {
            return sql;
        }
    }
}